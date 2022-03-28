package com.github.hueyra.mediax.widget.longimage;

public interface DecoderFactory<T> {
  /**
   * Produce a new instance of a decoder with type {@link T}.
   * @return a new instance of your decoder.
   */
  T make() throws IllegalAccessException, InstantiationException;
}
