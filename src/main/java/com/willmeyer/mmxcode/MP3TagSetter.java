package com.willmeyer.mmxcode;

import java.io.File;

import org.farng.mp3.*;
import org.farng.mp3.id3.*;

public class MP3TagSetter implements TagSetter {

	public String getSupportedFormat() {
		return "mp3";
	}

	public void setTags(File fileAbs, TagSet tags) throws Exception {
		MP3File mp3file = new MP3File(fileAbs.getAbsoluteFile());
	    TagOptionSingleton.getInstance().setDefaultSaveMode(TagConstant.MP3_FILE_SAVE_OVERWRITE);
	    TagOptionSingleton.getInstance().setOriginalSavedAfterAdjustingID3v2Padding(false);
	    ID3v2_3 id3 = new ID3v2_3();
	    if (tags.album != null) id3.setAlbumTitle(tags.album);
	    if (tags.trackTitle != null) id3.setSongTitle(tags.trackTitle);
	    if (tags.artist != null) id3.setLeadArtist(tags.artist);
	    try {
	    	if (tags.trackNum != null) id3.setTrackNumberOnAlbum(tags.trackNum);
	    } catch (Exception e) {
	    	Debug.println("Warning: Couldn't set tracknum tag on " + fileAbs.getAbsolutePath() + " (" + e.getMessage() + ")");
	    }
	    try {
	    	if (tags.genre != null) id3.setSongGenre(tags.genre);
		} catch (Exception e) {
			Debug.println("Warning: Couldn't set genre tag on " + fileAbs.getAbsolutePath() + " (" + e.getMessage() + ")");
		}
	    if (tags.year != null) id3.setYearReleased(tags.year);
	    mp3file.setID3v2Tag(id3);
	    mp3file.save();
	}

}
