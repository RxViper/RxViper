package com.dzaitsev.rxviper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Dmytro Zaitsev
 * @since 2017-Jul-16, 18:50
 */
abstract class RxViperInteractorImpl<RequestModel, ResponseModel> implements RxViperInteractor<RequestModel, ResponseModel> {
  private final ExecutorService executor = Executors.newCachedThreadPool();

  @Override
  public final void execute(@Nonnull OnNext<? super ResponseModel> onNext) {
    execute(onNext, OnError.NOT_IMPLEMENTED, OnComplete.EMPTY, null);
  }

  @Override
  public final void execute(@Nonnull OnNext<? super ResponseModel> onNext, @Nullable RequestModel requestModel) {
    execute(onNext, OnError.NOT_IMPLEMENTED, OnComplete.EMPTY, requestModel);
  }

  @Override
  public final void execute(@Nonnull OnNext<? super ResponseModel> onNext, @Nonnull OnError onError) {
    execute(onNext, onError, OnComplete.EMPTY, null);
  }

  @Override
  public final void execute(@Nonnull OnNext<? super ResponseModel> onNext, @Nonnull OnError onError,
      @Nullable RequestModel requestModel) {
    execute(onNext, onError, OnComplete.EMPTY, requestModel);
  }

  @Override
  public final void execute(@Nonnull OnNext<? super ResponseModel> onNext, @Nonnull OnError onError, @Nonnull OnComplete onComplete) {
    execute(onNext, onError, onComplete, null);
  }

  @Override
  public final void execute(@Nonnull OnNext<? super ResponseModel> onNext, @Nonnull OnError onError, @Nonnull OnComplete onComplete,
      @Nullable RequestModel requestModel) {
    final CyclicBarrier barrier = new CyclicBarrier(2);
    final BlockingQueue<Result<ResponseModel>> sharedQueue = new LinkedBlockingQueue<>();
    executor.execute(new Producer<>(sharedQueue, barrier, this, requestModel));
    executor.execute(new Consumer<>(sharedQueue, barrier, onNext, onError, onComplete));
  }

  @SuppressWarnings("WeakerAccess")
  @Nonnull
  protected abstract ResponseModel getData(@Nullable RequestModel requestModel);

  static final class Producer<Request, Response> implements Runnable {
    private final BlockingQueue<Result<Response>>          sharedQueue;
    private final CyclicBarrier                            barrier;
    private final RxViperInteractorImpl<Request, Response> interactor;
    private final Request                                  model;

    Producer(BlockingQueue<Result<Response>> sharedQueue, CyclicBarrier barrier, RxViperInteractorImpl<Request, Response> interactor,
        Request model) {
      this.sharedQueue = sharedQueue;
      this.barrier = barrier;
      this.interactor = interactor;
      this.model = model;
    }

    @Override
    public void run() {
      try {
        barrier.await();
        sharedQueue.put(Result.success(interactor.getData(model)));
      } catch (Throwable t) {
        try {
          sharedQueue.put(Result.<Response>error(t));
        } catch (InterruptedException e) {
          throw new RxViperException(e);
        }
      }
    }
  }

  static final class Consumer<T> implements Runnable {
    private final BlockingQueue<Result<T>> sharedQueue;
    private final CyclicBarrier            barrier;
    private final OnNext<? super T>        onNext;
    private final OnError                  onError;
    private final OnComplete               onComplete;

    Consumer(BlockingQueue<Result<T>> sharedQueue, CyclicBarrier barrier, OnNext<? super T> onNext, OnError onError,
        OnComplete onComplete) {
      this.sharedQueue = sharedQueue;
      this.barrier = barrier;
      this.onNext = onNext;
      this.onError = onError;
      this.onComplete = onComplete;
    }

    @Override
    public void run() {
      try {
        barrier.await();
        while (true) {
          final Result<T> result = sharedQueue.take();
          if (result.throwable == null) {
            onNext.onNext(result.value);
            onComplete.onComplete();
          } else {
            onError.onError(result.throwable);
            break;
          }
        }
      } catch (InterruptedException | BrokenBarrierException t) {
        throw new RxViperException(t);
      }
    }
  }

  private static final class Result<T> {
    @Nullable final T         value;
    @Nullable final Throwable throwable;

    private Result(@Nullable T value, @Nullable Throwable throwable) {
      this.value = value;
      this.throwable = throwable;
    }

    static <T> Result<T> success(T value) {
      return new Result<>(value, null);
    }

    static <T> Result<T> error(Throwable throwable) {
      return new Result<>(null, throwable);
    }
  }
}
