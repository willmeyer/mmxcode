package com.willmeyer.mmxcode;

import java.io.File;

/**
 * A mapper that transcodes a file from one format to another.
 */
public class TranscodingMapper extends FileMapper {

	public static final String NAME = "Transcode";
	
	protected Encoder encoder;
	protected Decoder decoder;
	protected TagSetter setter;
	protected TagGetter getter;
	protected String sourceExt;
	protected String destExt;
	
	public TranscodingMapper(String sourceExt, String destExt, Decoder decoder, Encoder encoder, TagGetter tagGetter, TagSetter tagSetter) {
		super(sourceExt, destExt);
		this.decoder = decoder;
		this.encoder = encoder;
		this.setter = tagSetter;
		this.getter = tagGetter;
		this.sourceExt = sourceExt;
		this.destExt = destExt;
	}
	
	@Override
	public String getMappedDestFileName(FileInfo srcFile, String destPath) {
		return destPath + File.separator + srcFile.fileNameNoExt + "." + this.destExt;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String traceSourceFromDest(FileInfo destFile, String sourcePath) {
		return sourcePath + File.separator + destFile.fileNameNoExt + "." + this.sourceExt;
	}

	private void cleanupFiles(File wavFile, File encodedFile) {
		if (wavFile.exists()) wavFile.delete();
		if (encodedFile.exists()) encodedFile.delete();
	}
	
	@Override
	public void map(FileInfo file1, FileInfo file2, long destFileTime) throws ActionException {
		File srcFile = new File(file1.fileAbs);
		File wavFile = new File(file1.pathAbs + File.separator + file1.fileNameNoExt + ".wav" + ".mmxtmp");
		File encodedFile = new File(file2.fileAbs);
		
		// Decode source file to wav
		try {
			decoder.decodeToWav(srcFile, null, wavFile.getAbsolutePath());
		} catch (Exception e) {
			this.cleanupFiles(wavFile, encodedFile);
			throw new ActionException("Unable to decode file.", srcFile.getAbsolutePath(), e);
		}
		if (!wavFile.exists()) {
			this.cleanupFiles(wavFile, encodedFile);
			throw new ActionException("Unable to decode file; decoded wav file not actually created!", wavFile.getAbsolutePath());
		}
		try {
			encoder.encodeFromWav(wavFile, null, encodedFile.getAbsolutePath());
		} catch (Exception e) {
			this.cleanupFiles(wavFile, encodedFile);
			throw new ActionException("Unable to encode file.", wavFile.getAbsolutePath(), e);
		}
		if (!encodedFile.exists()) {
			this.cleanupFiles(wavFile, encodedFile);
			throw new ActionException("Unable to encode file; encoded file not actually created!", encodedFile.getAbsolutePath());
		}
		wavFile.delete();
		if (getter != null && setter != null) {
			TagSet tags = null;
			try {
				tags = getter.getTags(srcFile);
			} catch (Exception e) {
				this.cleanupFiles(wavFile, encodedFile);
				throw new ActionException ("Unable to get tags from file.", srcFile.getAbsolutePath(), e);
			}
			try {
				setter.setTags(encodedFile, tags);
			} catch (Exception e) {
				this.cleanupFiles(wavFile, encodedFile);
				throw new ActionException ("Unable to add tags to file.", encodedFile.getAbsolutePath(), e);
			}
		}
		encodedFile.setLastModified(destFileTime);
	}

	@Override
	public boolean canMapSourceFile(String absFile) {
		return getFileExt(absFile).equalsIgnoreCase(srcExt);
	}
	
	@Override
	public boolean canTraceDestFile(String absFile) {
		return getFileExt(absFile).equalsIgnoreCase(destExt);
	}
}
