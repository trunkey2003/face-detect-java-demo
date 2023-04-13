package com.example.SADokobitGateway.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.SADokobitGateway.models.FaceDetectRequest;

import org.opencv.core.Rect;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Base64;

@RestController("/")
public class OpenCVController {
    // public Mat loadImage(String imagePath) {
    // Imgcodecs imageCodecs = new Imgcodecs();
    // return imageCodecs.imread(imagePath);
    // }

    public void saveImage(Mat imageMatrix, String targetPath) {
        Imgcodecs.imwrite(targetPath, imageMatrix);
    }

    @GetMapping("/")
    public String index() {
        return "Hello World";
    }

    @GetMapping("/api/v1/opencv")
    public Object test(@RequestParam String fileName) {
        try {
            // String sourceImagePath = "\\resources\\images\\lam.jpg";
            // String targetImagePath = "D:\\Users\\Documents\\Belly\\final\\lam.jpg";
            String opencvpath = System.getProperty("user.dir") + "\\files\\";
            String sourceImagePath = opencvpath + "/" + fileName;
            System.out.println("Load from: " + sourceImagePath);
            Mat loadedImage = Imgcodecs.imread(opencvpath + "/" + fileName);
            MatOfRect facesDetected = new MatOfRect();
            CascadeClassifier cascadeClassifier = new CascadeClassifier();
            int minFaceSize = Math.round(loadedImage.rows() * 0.1f);
            cascadeClassifier.load(opencvpath + "/haarcascades/haarcascade_frontalface_alt.xml");
            System.out.println(4);
            cascadeClassifier.detectMultiScale(loadedImage,
                    facesDetected,
                    1.1,
                    3,
                    Objdetect.CASCADE_SCALE_IMAGE,
                    new Size(minFaceSize, minFaceSize),
                    new Size());
            System.out.println("Faces detected");
            Rect[] facesArray = facesDetected.toArray();
            for (Rect face : facesArray) {
                Imgproc.rectangle(loadedImage, face.tl(), face.br(), new Scalar(0, 0, 255),
                        3);
            }
            System.out.println("Saving here");
            String resultPath = opencvpath + "/" + "[result]" + fileName;
            saveImage(loadedImage, resultPath);
            return "Success store at " + resultPath;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Error";
        }
    }

    @GetMapping("/api/v1/opencv/load-lib-manually")
    public Object loadLib() {
        try {
            String opencvpath = System.getProperty("user.dir") + "\\files\\";
            System.load(opencvpath + Core.NATIVE_LIBRARY_NAME + ".dll");
            System.out.println(opencvpath + Core.NATIVE_LIBRARY_NAME + ".dll");
            return "Success";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Error";
        }
    }

    @PostMapping("/api/v1/opencv/face-detect")
    public Object faceDetect(@RequestBody FaceDetectRequest requestBody) {
        String base64String = requestBody.getBase64String();
        String fileName = requestBody.getFileName();
        try {
            // String sourceImagePath = "\\resources\\images\\lam.jpg";
            // String targetImagePath = "D:\\Users\\Documents\\Belly\\final\\lam.jpg";
            byte[] imageBytes = Base64.getDecoder().decode(base64String);
            String opencvpath = System.getProperty("user.dir") + "\\files\\";
            Mat loadedImage = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
            MatOfRect facesDetected = new MatOfRect();
            CascadeClassifier cascadeClassifier = new CascadeClassifier();
            int minFaceSize = Math.round(loadedImage.rows() * 0.1f);
            cascadeClassifier.load(opencvpath + "/haarcascades/haarcascade_frontalface_alt.xml");
            System.out.println(4);
            cascadeClassifier.detectMultiScale(loadedImage,
                    facesDetected,
                    1.1,
                    3,
                    Objdetect.CASCADE_SCALE_IMAGE,
                    new Size(minFaceSize, minFaceSize),
                    new Size());
            System.out.println("Faces detected");
            Rect[] facesArray = facesDetected.toArray();
            for (Rect face : facesArray) {
                Imgproc.rectangle(loadedImage, face.tl(), face.br(), new Scalar(0, 0, 255),
                        3);
            }
            System.out.println("Saving here");
            String resultPath = opencvpath + "/" + "[result]-" + fileName;
            saveImage(loadedImage, resultPath);
            return "Success store at " + resultPath;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Error";
        }
    }

    @GetMapping("/api/v1/opencv/get-result-image")
    public ResponseEntity<?> getResultImage(@RequestParam String fileName) {
        try {
            String opencvpath = System.getProperty("user.dir") + "\\files\\";
            String filePath = opencvpath + Paths.get("/dokobit-sign-files")
                    + Paths.get("/" + fileName);
            File file = new File(filePath);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
            return ResponseEntity.ok()
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }
}
