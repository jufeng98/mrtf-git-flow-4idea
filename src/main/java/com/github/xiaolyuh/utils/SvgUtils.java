package com.github.xiaolyuh.utils;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class SvgUtils {

    public static byte[] convertToImageBytes(InputStream inputStream) {
        TranscoderInput transcoderInput = new TranscoderInput(inputStream);
        PNGTranscoder transcoder = new PNGTranscoder();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);
        try {
            transcoder.transcode(transcoderInput, transcoderOutput);
            outputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return outputStream.toByteArray();
    }

}
