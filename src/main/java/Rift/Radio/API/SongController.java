package Rift.Radio.API;

import Rift.Radio.Model.Song;
import Rift.Radio.Repository.SongRepository;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/songs")
public class SongController {
    @Autowired
    private SongRepository songRepository;

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @PostMapping("/upload")
    @ResponseBody
    public Song uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Save the file to local storage
            String fileName = file.getOriginalFilename();
            String filePath = "/home/stykle/Documents/MusicApplicationBetaTesting/sample/" + fileName;
            file.transferTo(new File(filePath));

            // Save the song path to the database
            Song song = new Song();
            song.setName(fileName);
            song.setPath(filePath);
            songRepository.save(song);

            return song;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/{id}/path")
    @ResponseBody
    public String getSongPath(@PathVariable Long id) {
        Optional<Song> songOptional = songRepository.findById(id);
        if (songOptional.isPresent()) {
            return songOptional.get().getPath();
        } else {
            throw new NotFoundException("Song not found");
        }
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> getSongFile(@PathVariable Long id) {
        Optional<Song> songOptional = songRepository.findById(id);
        if (songOptional.isPresent()) {
            Song song = songOptional.get();
            try {
                File file = new File(song.getPath());
                Resource resource = new UrlResource(file.toURI());

                if (resource.exists()) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                            .body(resource);
                } else {
                    throw new NotFoundException("Song file not found");
                }
            } catch (IOException e) {
                throw new NotFoundException("Song file not found");
            }
        } else {
            throw new NotFoundException("Song not found");
        }
    }


    // Other controller methods for retrieving songs from the database
}