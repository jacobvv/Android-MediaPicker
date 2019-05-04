package org.jacobvv.mediapicker.data;

import org.jacobvv.mediapicker.data.model.Album;

import java.util.Comparator;

/**
 * Comparator for albums, All media is first, then, camera and screenshots follow.
 *
 * @author Jacob
 * @date 18-1-3
 */
public class AlbumComparator implements Comparator<Album> {

    @Override
    public int compare(Album o1, Album o2) {
        if (o1 == null || o2 == null) {
            throw new IllegalArgumentException("Null Album object in comparator.");
        }
        return getIndex(o1) - getIndex(o2);
    }

    private int getIndex(Album album) {
        if (album.getId() == Album.BUCKET_ID_ALL) {
            return -1;
        } else if (Album.BUCKET_NAME_CAMERA.equalsIgnoreCase(album.getName())) {
            return 0;
        } else if (Album.BUCKET_NAME_SCREENSHOTS.equalsIgnoreCase(album.getName())) {
            return 1;
        } else {
            return 10;
        }
    }
}
