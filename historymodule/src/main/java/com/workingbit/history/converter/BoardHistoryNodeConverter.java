package com.workingbit.history.converter;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.UTF8JsonGenerator;

import java.io.OutputStream;

/**
 * Created by Aleksey Popryaduhin on 12:21 13/08/2017.
 */
public class BoardHistoryNodeConverter extends UTF8JsonGenerator {

  public BoardHistoryNodeConverter(IOContext ctxt, int features, ObjectCodec codec, OutputStream out) {
    super(ctxt, features, codec, out);
  }

  public BoardHistoryNodeConverter(IOContext ctxt, int features, ObjectCodec codec, OutputStream out, byte[] outputBuffer, int outputOffset, boolean bufferRecyclable) {
    super(ctxt, features, codec, out, outputBuffer, outputOffset, bufferRecyclable);
  }
}
