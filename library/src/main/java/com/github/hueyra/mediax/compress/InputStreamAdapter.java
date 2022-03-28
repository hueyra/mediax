package com.github.hueyra.mediax.compress;

import java.io.IOException;
import java.io.InputStream;

public abstract class InputStreamAdapter implements InputStreamProvider {

  private InputStream inputStream;

  @Override
  public InputStream open() throws IOException {
    close();
    inputStream = openInternal();
    return inputStream;
  }

  public abstract InputStream openInternal() throws IOException;

  @Override
  public void close() {
    if (inputStream != null) {
      try {
        inputStream.close();
      } catch (IOException ignore) {
      }finally {
        inputStream = null;
      }
    }
  }
}