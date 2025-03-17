/*
 * This file is part of the MoreThanPomodoro project.
 * Please refer to the project's README.md file for additional details.
 * https://github.com/turkerozturk/morethanpomodoro
 *
 * Copyright (c) 2025 Turker Ozturk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.en.html>.
 */
package com.turkerozturk.chatgpt;

import com.iheartradio.m3u8.Encoding;
import com.iheartradio.m3u8.Format;
import com.iheartradio.m3u8.PlaylistParser;
import com.iheartradio.m3u8.PlaylistWriter;
import com.iheartradio.m3u8.data.MediaPlaylist;
import com.iheartradio.m3u8.data.Playlist;
import com.iheartradio.m3u8.data.TrackData;
import com.iheartradio.m3u8.data.TrackInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class M3U8PlaylistManager {

    // BASLA singleton
    private static M3U8PlaylistManager instance; // Tekil nesne referansı

    // Constructor'ı private yaparak dışarıdan nesne oluşturulmasını engelliyoruz
    private M3U8PlaylistManager() {
        // İstenirse burada ek işlemler yapılabilir
    }

    public static M3U8PlaylistManager getInstance() {
        if (instance == null) {
            synchronized (M3U8PlaylistManager.class) {
                if (instance == null) {
                    instance = new M3U8PlaylistManager();
                }
            }
        }
        return instance;
    }
    // BITTI singleton

    // Mevcut playlist dosyasının yolu
    private String currentPlaylistPath = null;

    // Kütüphanede 'Playlist' hem MasterPlaylist hem de MediaPlaylist bilgisini tutar
    private Playlist currentPlaylist = null;

    // Sadece MediaPlaylist senaryosunda track verilerini yönetiyoruz
    // (MasterPlaylist senaryosu varsa, hasMediaPlaylist() false dönecektir)
    private List<TrackData> trackList = new ArrayList<>();

    private TrackData currentTrack;

    private int currentTrackIndex = 0; // Seçili track'in indeksi


    /**
     * Yeni, boş bir playlist oluşturur. (MediaPlaylist olarak)
     */
    public void newPlaylist() {
        currentPlaylistPath = null;
        trackList.clear();

        // Boş bir MediaPlaylist için builder
        MediaPlaylist emptyMediaPlaylist = new MediaPlaylist.Builder()
                .withTracks(trackList)
                // withTargetDuration vs. gibi ayarlar isterseniz burada ekleyin
                .build();

        // Playlist builder
        currentPlaylist = new Playlist.Builder()
                .withMediaPlaylist(emptyMediaPlaylist)
                // .withCompatibilityVersion(PlaylistVersion.TWELVE.getVersion()) // 12 veya uygun versiyon // boyle bir sinif bulamadi intellij
                .build();
    }

    /**
     * Mevcut playlist'i path'e kaydeder.
     * Eğer path set edilmemişse, 'savePlaylistAs()' şeklinde davranır.
     */
    public void savePlaylist() throws Exception {
        if (currentPlaylist == null) {
            throw new IllegalStateException("Playlist henüz oluşturulmamış veya açılmamış.");
        } else {
            writePlaylistToFile(currentPlaylistPath);

        }

    }

            /*
        if (currentPlaylistPath == null) {

            // GEREK KALMADI, BU "SAVE AS" ISINI PLAYLIST PANELDE YAPTIRIYORUZ.
            // Path yoksa Save As gibi davran
            // savePlaylistAs();
            return;
        }
        */

    /**
     * Kullanıcıdan yeni bir dosya adı alıp (File Chooser) playlist'i oraya kaydeder.
     */
    /*
    public void savePlaylistAs() throws Exception {
        if (currentPlaylist == null) {
            throw new IllegalStateException("Playlist henüz oluşturulmamış veya açılmamış.");
        }

        // TODO: GUI tarafında File Chooser ile yeni bir dosya seçtirin
        // Örnek:
        // JFileChooser fc = new JFileChooser();
        // if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
        //     File chosenFile = fc.getSelectedFile();
        //     currentPlaylistPath = chosenFile.getAbsolutePath();
        // }

        // Demo amaçlı (GUI yok): Elle path veriyoruz:
        currentPlaylistPath = "C:/tmp/myNewPlaylist.m3u8"; // Örnek

        writePlaylistToFile(currentPlaylistPath);
    }
*/


    /**
     * Var olan bir .m3u8 dosyasını açar ve trackList'e yükler.
     */
    public void openPlaylist() throws Exception {
        // TODO: Burada da File Chooser ile .m3u8 dosyası seçtirebilirsiniz
        // Demo için sabit path
        //String path = "C:/tmp/playlist1.m3u8";

        try (InputStream inputStream = Files.newInputStream(Paths.get(currentPlaylistPath))) {
            PlaylistParser parser = new PlaylistParser(inputStream, Format.EXT_M3U, Encoding.UTF_8);
            //System.out.println("zzz: " + currentPlaylistPath);

            // Okunan playlist'i hafızaya al
            currentPlaylist = parser.parse();
            //currentPlaylistPath = path;

            // Eger MediaPlaylist varsa trackList'i doldur
            if (currentPlaylist.hasMediaPlaylist()) {
                MediaPlaylist mp = currentPlaylist.getMediaPlaylist();
                // Orijinal liste unmodifiable olabilir, bu yüzden kopyasını alıyoruz
                trackList = new ArrayList<>(mp.getTracks());
            } else {
                // MasterPlaylist senaryosu
                trackList.clear();
            }
        }
    }

    /**
     * Seçtiğimiz tek bir mp3 dosyasını eklemek için
     * (GUI tarafında File Chooser'la mp3 dosyası seçersiniz, buraya parametre olarak verirsiniz).
     */
    public void addTrackToList(TrackData trackData) {
        if (trackData == null) {
            throw new IllegalArgumentException("trackData null olamaz");
        }
        trackList.add(trackData);
    }

    /**
     * Mevcut playlist'e, klasörden birden çok mp3 dosyasını eklemek isterseniz
     */
    public void addMultipleTracksFromFolder(File folder) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("Seçilen dosya bir klasör değil!");
        }
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (isMp3File(f)) {


                // https://www.jthink.net/jaudiotagger/examples_id3.jsp

                TrackInfo trackInfo;

                /*
                MP3File ffff      = null;
                try {
                    ffff = (MP3File) AudioFileIO.read(f);
                    MP3AudioHeader audioHeader = (MP3AudioHeader) ffff.getAudioHeader();
                    audioHeader.getTrackLength();
                    audioHeader.getSampleRateAsNumber();

                    trackInfo = new TrackInfo(audioHeader.getTrackLength(), f.getName());
                    //mp3AudioHeader.getChannels();
                    //mp3AudioHeader.isVariableBitRate();
                } catch (CannotReadException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (TagException e) {
                    throw new RuntimeException(e);
                } catch (ReadOnlyFileException e) {
                    throw new RuntimeException(e);
                } catch (InvalidAudioFrameException e) {
                    throw new RuntimeException(e);
                }
                */
                trackInfo = new TrackInfo(0.0f, f.getName());


                TrackData td = new TrackData.Builder()
                        .withTrackInfo(trackInfo)
                        .withUri(f.getAbsolutePath())
                        .build();
                trackList.add(td);
            }
        }
    }

    /**
     * id (index) ile listeden şarkıyı kaldırır
     */
    public void removeTrackFromList(int listId) {
        if (listId < 0 || listId >= trackList.size()) {
            throw new IndexOutOfBoundsException("Geçersiz index: " + listId);
        }
        trackList.remove(listId);
    }

    /**
     * Seçili entry'i bir üst sıraya taşır
     */
    public void moveTrackUp(int listId) {
        if (listId > 0 && listId < trackList.size()) {
            Collections.swap(trackList, listId, listId - 1);
        }
    }

    /**
     * Seçili entry'i bir alt sıraya taşır
     */
    public void moveTrackDown(int listId) {
        if (listId >= 0 && listId < trackList.size() - 1) {
            Collections.swap(trackList, listId, listId + 1);
        }
    }

    /**
     * Belirli indexteki track'in bilgisini (title, duration, vs.) döndürür
     */
    public TrackInfo trackInfo(int listId) {
        if (listId < 0 || listId >= trackList.size()) {
            throw new IndexOutOfBoundsException("Geçersiz index: " + listId);
        }
        TrackData td = trackList.get(listId);
        return td.getTrackInfo(); // duration, title gibi bilgilere buradan erişebilirsiniz
    }

    /**
     * Mevcut 'trackList' üzerinde yapılan değişiklikleri tekrar 'currentPlaylist' nesnesine yansıtıp
     * dosyaya yazar.
     */
    private void writePlaylistToFile(String path) throws Exception {
        if (currentPlaylist == null) {
            throw new IllegalStateException("Playlist yok, newPlaylist() veya openPlaylist() çağırmalısınız.");
        }
        // MediaPlaylist'i güncelle
        MediaPlaylist updatedMediaPlaylist = currentPlaylist.getMediaPlaylist()
                .buildUpon()
                .withTracks(trackList) // yeni trackList ekliyoruz
                .build();

        Playlist updatedPlaylist = currentPlaylist.buildUpon()
                .withMediaPlaylist(updatedMediaPlaylist)
                .build();

        for (TrackData trackData : updatedPlaylist.getMediaPlaylist().getTracks()) {
            System.out.println("writePlaylistToFile: " + trackData.getUri());
            if (trackData.getTrackInfo() != null) {
                float duration = trackData.getTrackInfo().duration;
                System.out.println("duration: " + duration);
            } else {
                // TrackInfo null olduğunda yapılacak işlem
                System.out.println("TrackInfo is null, skipping...");
            }
        }

        if (Files.exists(Paths.get(path))) {
            Files.delete(Paths.get(path));
            System.out.println("dosya silindi: " + path);
        }

        try (OutputStream outputStream = new FileOutputStream(path)) {
            PlaylistWriter writer = new PlaylistWriter(outputStream, Format.EXT_M3U, Encoding.UTF_8);
            writer.write(updatedPlaylist);


        }


        // Kayıt başarılı olunca currentPlaylist'i ve path'i güncelliyoruz
        currentPlaylist = updatedPlaylist;
        currentPlaylistPath = path;


    }

    /**
     * MP3 uzantısı kontrolü (çok basit)
     */
    private boolean isMp3File(File f) {
        String name = f.getName().toLowerCase();
        return (name.endsWith(".mp3"));
    }

    public String getCurrentPlaylistPath() {
        return currentPlaylistPath;
    }

    public void setCurrentPlaylistPath(String currentPlaylistPath) {
        this.currentPlaylistPath = currentPlaylistPath;
    }

    public List<TrackData> getTrackList() {
        return trackList;
    }

    public void setTrackList(List<TrackData> trackList) {
        this.trackList = trackList;
    }


    public TrackData nextTrack() {
        if (trackList.isEmpty()) {
            return null;
        }
        currentTrackIndex = (currentTrackIndex + 1) % trackList.size(); // Döngüsel ilerleme
        System.out.println("M3...java: " + currentTrackIndex + "/" + trackList.size());
        return trackList.get(currentTrackIndex);
    }

    public TrackData previousTrack() {
        if (trackList.isEmpty()) {
            return null;
        }
        currentTrackIndex = (currentTrackIndex - 1 + trackList.size()) % trackList.size(); // Döngüsel geri gitme
        return trackList.get(currentTrackIndex);
    }

    public TrackData getCurrentTrack() {
        if (trackList.isEmpty() || currentTrackIndex < 0 || currentTrackIndex >= trackList.size()) {
            return null;
        }
        return trackList.get(currentTrackIndex);
    }

    public void setCurrentTrack(TrackData trackData) {
        currentTrack = trackData;
    }

    public TrackData firstTrack() {
        if (trackList.isEmpty()) {
            return null;
        }
        currentTrackIndex = 0;
        return trackList.get(currentTrackIndex);
    }

    public TrackData lastTrack() {
        if (trackList.isEmpty()) {
            return null;
        }
        currentTrackIndex = trackList.size() - 1;
        return trackList.get(currentTrackIndex);
    }
/*
    public TrackData selectedTrack(int index) {
        if (index < 0 || index >= trackList.size()) {
            throw new IndexOutOfBoundsException("Geçersiz index: " + index);
        }
        currentTrackIndex = index;

        return trackList.get(currentTrackIndex);
    }
*/

    public void updateTrackInfo() {
        // TODO
    }

    public int getCurrentTrackIndex() {
        return currentTrackIndex;
    }

    public void setCurrentTrackIndex(int currentTrackIndex) {
        this.currentTrackIndex = currentTrackIndex;
    }

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(Playlist currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }
}
