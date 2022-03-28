package com.github.hueyra.mediax.widget.longimage;

import androidx.annotation.NonNull;

public class CompatDecoderFactory <T> implements DecoderFactory<T> {
  private Class<? extends T> clazz;

  public CompatDecoderFactory(@NonNull Class<? extends T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public T make() throws IllegalAccessException, InstantiationException {
    return clazz.newInstance();
  }
}
