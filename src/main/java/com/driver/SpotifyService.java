package com.driver;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class SpotifyService {

    //Auto-wire will not work in this case, no need to change this and add autowire

    SpotifyRepository spotifyRepository = new SpotifyRepository();

    public static User createUser(String name, String mobile){
       return  SpotifyRepository.createUser(name,mobile);
    }

    public static Artist createArtist(String name) {
       return SpotifyRepository.createArtist(name);
    }

    public static Album createAlbum(String title, String artistName) {
       return SpotifyRepository.createAlbum(title,artistName);
    }

    public static Song createSong(String title, String albumName, int length) throws Exception {
        return SpotifyRepository.createSong(title,albumName,length);
    }

    public static Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
       return SpotifyRepository.createPlaylistOnLength(mobile,title,length);
    }

    public static Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
     return SpotifyRepository.createPlaylistOnName(mobile,title,songTitles);
    }

    public static Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        return SpotifyRepository.findPlaylist(mobile,playlistTitle);
    }

    public static Song likeSong(String mobile, String songTitle) throws Exception {
      return SpotifyRepository.likeSong(mobile,songTitle);
    }

    public static String mostPopularArtist() {
        return SpotifyRepository.mostPopularArtist();
    }

    public static String mostPopularSong() {
    return SpotifyRepository.mostPopularSong();
    }
}
