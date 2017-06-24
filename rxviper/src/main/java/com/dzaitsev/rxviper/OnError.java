package com.dzaitsev.rxviper;

import rx.exceptions.OnErrorNotImplementedException;

@SuppressWarnings("WeakerAccess")
public interface OnError {
  OnError NOT_IMPLEMENTED = throwable -> {
    throw new OnErrorNotImplementedException(throwable);
  };

  void onError(Throwable throwable);
}
