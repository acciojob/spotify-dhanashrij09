package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public static HashMap<Artist, List<Album>> artistAlbumMap;
    public static HashMap<Album, List<Song>> albumSongMap;
    public static HashMap<Playlist, List<Song>> playlistSongMap;
    public static HashMap<Playlist, List<User>> playlistListenerMap;
    public static HashMap<User, Playlist> creatorPlaylistMap;
    public static HashMap<User, List<Playlist>> userPlaylistMap;
    public static HashMap<Song, List<User>> songLikeMap;

    public static List<User> users;
    public static List<Song> songs;
    public static List<Playlist> playlists;
    public static List<Album> albums;
    public static List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public static User createUser(String name, String mobile) {
        //create new user
        User user = new User(name,mobile);
        //update user list and map
        users.add(user);
        userPlaylistMap.put(user,new ArrayList<>());
        return user;
    }

    public static Artist createArtist(String name) {
        //create new artist
        Artist artist = new Artist(name);
        //update artist list and map
        artists.add(artist);
        artistAlbumMap.put(artist,new ArrayList<>());
        return artist;
    }

    public static Album createAlbum(String title, String artistName) {
        //checking if artistName already exists in list
        Artist artist = null;
        for (Artist a : artists) {
            if (a.getName().equals(artistName)) {
                artist = a;
                break;
            }
        }
        // else create new artist
        if (artist == null) {
            artist = new Artist(artistName);
            artists.add(artist);
        }
        //creating new album
        Album album = new Album(title);
        //update the albums list
        albums.add(album);
        //update the artistAlbumMap
        if (!artistAlbumMap.containsKey(artist)) {
            artistAlbumMap.put(artist, new ArrayList<>());
        }
        artistAlbumMap.get(artist).add(album);
        return album;
    }

    public static Song createSong(String title, String albumName, int length) throws Exception{
      // check if album exists in database
        Album album = null;
        for (Album a : albums) {
            if (a.getTitle().equals(albumName)) {
                album = a;
                break;
            }
        }
        //if album does not exist
        if (album == null) {
            throw new Exception("Album does not exist");
        }
        Song song = new Song(title, length);
        songs.add(song);
        if (!albumSongMap.containsKey(album)) {
            albumSongMap.put(album, new ArrayList<>());
        }
        albumSongMap.get(album).add(song);
        return song;
    }

    public static Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = null;
        //check if user exists
        for (User u : users) {
            if (u.getMobile().equals(mobile)) {
                user = u;
                break;
            }
        }
        //if user does not exist
        if (user == null) {
            throw new Exception("User does not exist");
        }
        //create new playlist
        Playlist playlist = new Playlist(title);
        //update playlist and map
        playlists.add(playlist);
        if (!playlistSongMap.containsKey(playlist)) {
            playlistSongMap.put(playlist, new ArrayList<>());
        }
        //adding songs of equal length to playlist
        for (Song song : songs) {
            if (song.getLength() == length) {
                playlistSongMap.get(playlist).add(song);
            }
        }
        //update playlistListenerMap
        if (!playlistListenerMap.containsKey(playlist)) {
            playlistListenerMap.put(playlist, new ArrayList<>());
        }
        // add user or update user list of map
        if (!playlistListenerMap.get(playlist).contains(user)) {
            playlistListenerMap.get(playlist).add(user);
        }
        //update userPlayListMap
        if (!userPlaylistMap.containsKey(user)) {
            userPlaylistMap.put(user, new ArrayList<>());
        }
        userPlaylistMap.get(user).add(playlist);
        //update creatorPlaylistMap
        creatorPlaylistMap.put(user, playlist);

        return playlist;
    }

    public static Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = null;
        //check if user exists
        for (User u : users) {
            if (u.getMobile().equals(mobile)) {
                user = u;
                break;
            }
        }
        //if user does not exist
        if (user == null) {
            throw new Exception("User does not exist");
        }
        //create playlist and update list
        Playlist newPlaylist = new Playlist(title);
        playlists.add(newPlaylist);

        // Add songs to playlist
        List<Song> songsToAdd = new ArrayList<>();
        // Find song in songsList with matching title
        for (String titles : songTitles) {
            Song song = null;
            for (Song s : songs) {
                if (s.getTitle().equals(titles)) {
                    song = s;
                    break;
                }
            }
            // If song exists, add it to songsToAdd list
            if (song != null) {
                songsToAdd.add(song);
            }
        }
        // Add songsToAdd list to playlistSongMap
        playlistSongMap.put(newPlaylist, songsToAdd);

        // Add new playlist to userPlaylistMap for user
        List<Playlist> userPlaylists = userPlaylistMap.get(user);
        if (userPlaylists == null) {
            userPlaylists = new ArrayList<>();
        }
        userPlaylists.add(newPlaylist);
        userPlaylistMap.put(user, userPlaylists);
        return  newPlaylist;
    }

    public static Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = null;
        //check if user exists
        for (User u : users) {
            if (u.getMobile().equals(mobile)) {
                user = u;
                break;
            }
        }
        //if user does not exist
        if (user == null) {
            throw new Exception("User does not exist");
        }
        // Check if the playlist exists
        Playlist playlist = null;
        for (Playlist p : playlists) {
            if (p.getTitle().equals(playlistTitle)) {
                playlist = p;
                break;
            }
        }

        if (playlist == null) {
            throw new IllegalArgumentException("Playlist does not exist");
        }

        // Check if the user is the creator or already a listener
        if (playlist.getTitle().equals(playlistTitle)) {
            return playlist;
        }
        // Add the user as a listener and update the playlist
        List<User> listeners = playlistListenerMap.get(playlist);
        listeners.add(user);
        playlistListenerMap.put(playlist, listeners);

        List<Playlist> userPlaylists = userPlaylistMap.get(user);
        userPlaylists.add(playlist);
        userPlaylistMap.put(user, userPlaylists);

        return playlist;
    }

    public static Song likeSong(String mobile, String songTitle) throws Exception {
        User user = null;
        //check if user exists
        for (User u : users) {
            if (u.getMobile().equals(mobile)) {
                user = u;
                break;
            }
        }
        //if user does not exist
        if (user == null) {
            throw new Exception("User does not exist");
        }
       Song song = null;
        for (Song s : songs) {
            if (s.getTitle().equals(songTitle)) {
                song = s;
                break;
            }
        }

        if (song == null) {
            throw new IllegalArgumentException("Song does not exist");
        }
        // Check if the user has already liked the song
        List<User> usersLiked = songLikeMap.get(song);
        if (usersLiked != null && usersLiked.contains(user)) {
            return song;
        }

        // Add the user to the list of users who liked the song
        if (usersLiked == null) {
            usersLiked = new ArrayList<>();
            songLikeMap.put(song, usersLiked);
        }
        usersLiked.add(user);

        // Like the song
        songLikeMap.get(song).add(user);

        // Like the corresponding artist
        Artist artist = null;
        for (Artist a : artists) {
            artist = a;

        }
        artist.setLikes(artist.getLikes() + 1);

        return song;
    }

    public static String mostPopularArtist() {
        int maxLikes = -1;
        Artist popularArtist = null;
        for (Artist artist : artists) {
            if (artist.getLikes() > maxLikes) {
                maxLikes = artist.getLikes();
                popularArtist = artist;
            }
        }
        return popularArtist.getName();
    }

    public static String mostPopularSong() {
        int maxLikes = -1;
        String popularSongTitle = null;
        for (Song song : songs) {
           // int likes = songLikeMap.getOrDefault(song, new ArrayList<>()).size();
            if (song.getLikes() > maxLikes) {
                maxLikes = song.getLikes();
                popularSongTitle = song.getTitle();
            }
        }
        return popularSongTitle;
    }
}
