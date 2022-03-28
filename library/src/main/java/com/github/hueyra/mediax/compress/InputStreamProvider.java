package com.github.hueyra.mediax.compress;

import com.github.hueyra.mediax.entity.LocalMedia;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamProvider {

    InputStream open() throws IOException;

    void close();

    String getPath();

    LocalMedia getMedia();

}
