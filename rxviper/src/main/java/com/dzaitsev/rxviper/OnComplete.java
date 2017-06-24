package com.dzaitsev.rxviper;

@SuppressWarnings("WeakerAccess")
public interface OnComplete {
  OnComplete EMPTY = new OnComplete() {
    @Override
    public void onComplete() { }
  };

  void onComplete();
}
