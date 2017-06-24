package com.dzaitsev.rxviper;

@SuppressWarnings("WeakerAccess")
public interface OnError {
  OnError NOT_IMPLEMENTED = new OnError() {
    @Override
    public void onError(Throwable throwable) {
      throw new UnsupportedOperationException(throwable);
    }
  };

  void onError(Throwable throwable);
}
