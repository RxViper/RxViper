package com.dzaitsev.rxviper;

@SuppressWarnings("WeakerAccess")
public interface OnComplete {
  OnComplete EMPTY = () -> { };

  void onComplete();
}
