package me.swirtzly.regen.client.skin;

import me.swirtzly.regen.Regeneration;
import me.swirtzly.regen.config.RegenConfig;
import me.swirtzly.regen.util.PlayerUtil;
import me.swirtzly.regen.util.RegenUtil;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static me.swirtzly.regen.util.RegenUtil.getJsonFromURL;

public class CommonSkin {

    public static final File SKIN_DIRECTORY = new File(RegenConfig.COMMON.skinDir.get() + "/Regeneration Data/skins/");
    public static final File SKIN_DIRECTORY_STEVE = new File(SKIN_DIRECTORY, "/steve");
    public static final File SKIN_DIRECTORY_ALEX = new File(SKIN_DIRECTORY, "/alex");
    public static final File SKIN_DIRECTORY_MALE = new File(SKIN_DIRECTORY, "/timelord/male");
    public static final File SKIN_DIRECTORY_FEMALE = new File(SKIN_DIRECTORY, "/timelord/female");
    public static File TRENDING_ALEX = new File(SKIN_DIRECTORY_ALEX + "/namemc");
    public static File TRENDING_STEVE = new File(SKIN_DIRECTORY_STEVE + "/namemc");

    public static ResourceLocation fileTotexture(File file) {
        NativeImage nativeImage = null;
        try {
            nativeImage = NativeImage.read(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SkinHandler.loadImage(nativeImage);
    }

    //Choose a random PNG from a folder
    public static File chooseRandomSkin(Random rand, boolean isAlex, boolean isTimelord) {
        File skins = isAlex ? SKIN_DIRECTORY_ALEX : SKIN_DIRECTORY_STEVE;
        if (isTimelord) {
            skins = isAlex ? SKIN_DIRECTORY_FEMALE : SKIN_DIRECTORY_MALE;
        }
        Collection< File > folderFiles = FileUtils.listFiles(skins, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        folderFiles.removeIf(file -> !file.getName().endsWith(".png"));
        return (File) folderFiles.toArray()[rand.nextInt(folderFiles.size())];
    }

    //Get a list of skins from namemc url
    public static ArrayList< String > getSkins(String downloadUrl) throws IOException {
        ArrayList< String > skins = new ArrayList<>();
        BufferedReader br = null;

        try {
            URL url = new URL(downloadUrl);
            URLConnection uc = url.openConnection();
            uc.connect();
            uc = url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
            br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("<a href=\"/skin/")) {
                    String downloadLine = line.replaceAll("<a href=\"/skin/", "").replaceAll("\">", "").replaceAll("        ", "");
                    skins.add("https://namemc.com/texture/" + downloadLine + ".png");
                }
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return skins;
    }

    public static void createDefaultFolders() throws IOException {

        if (!SKIN_DIRECTORY.exists()) {
            FileUtils.forceMkdir(SKIN_DIRECTORY);
        }

        if (!SKIN_DIRECTORY_ALEX.exists()) {
            FileUtils.forceMkdir(SKIN_DIRECTORY_ALEX);
        }

        if (!SKIN_DIRECTORY_STEVE.exists()) {
            FileUtils.forceMkdir(SKIN_DIRECTORY_STEVE);
        }

        if (!SKIN_DIRECTORY_STEVE.exists()) {
            FileUtils.forceMkdir(SKIN_DIRECTORY_FEMALE);
        }

        if (!SKIN_DIRECTORY_STEVE.exists()) {
            FileUtils.forceMkdir(SKIN_DIRECTORY_MALE);
        }

    }

    /**
     * @param url      - URL to download image from
     * @param filename - Filename of the image [SHOULD NOT CONTAIN FILE EXTENSION, PNG IS SUFFIXED FOR YOU]
     * @throws IOException
     */
    public static void downloadSkins(URL url, String filename, File alexDir, File steveDir) throws IOException {
        URLConnection uc = url.openConnection();
        uc.connect();
        uc = url.openConnection();
        uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
        BufferedImage img = ImageIO.read(uc.getInputStream());
        File file = isAlexSkin(img) ? alexDir : steveDir;
        if (!file.exists()) {
            file.mkdirs();
        }

        if (!steveDir.exists()) {
            steveDir.mkdirs();
        }

        if (!alexDir.exists()) {
            alexDir.mkdirs();
        }

        Regeneration.LOG.warn("URL: {} || Name: {} || Path: {}", url.toString(), filename, file.getPath());
        ImageIO.write(img, "png", new File(file, filename + ".png"));
    }

    public static void downloadSkinsSpecific(URL url, String filename, File specific) throws IOException {
        URLConnection uc = url.openConnection();
        uc.connect();
        uc = url.openConnection();
        uc.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
        BufferedImage img = ImageIO.read(uc.getInputStream());
        //  img= toBlackAndWhite(img);
        File file = specific;
        if (!file.exists()) {
            file.mkdirs();
        }

        Regeneration.LOG.warn("URL: {} || Name: {} || Path: {}", url.toString(), filename, file.getPath());
        ImageIO.write(img, "png", new File(file, filename + ".png"));
    }

    public static BufferedImage toBlackAndWhite(BufferedImage img) {
        BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        return op.filter(img, gray);
    }

    public static void internalSkinsDownload() throws IOException {
        if (!RegenConfig.SKIN.downloadInteralSkins.get() || !RegenUtil.doesHaveInternet()) return;

        File drWhoDir = new File(SKIN_DIRECTORY_ALEX + "/doctor_who");

        long attr = drWhoDir.lastModified();
        if (System.currentTimeMillis() - attr >= 86400000 || Objects.requireNonNull(drWhoDir.list()).length == 0) {
            Regeneration.LOG.info("Re-Downloading Internal Skins");
            String PACKS_URL = "https://raw.githubusercontent.com/Swirtzly/Regeneration/skins/index.json";
            String[] links = Regeneration.GSON.fromJson(getJsonFromURL(PACKS_URL), String[].class);
            for (String link : links) {
                unzipSkinPack(link);
            }
        }
    }

    public static boolean isAlexSkin(BufferedImage image) {

        for (int i = 0; i < 8; i++) {
            if (!hasAlpha(54, i + 20, image) || !hasAlpha(55, i + 20, image)) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasAlpha(int x, int y, BufferedImage image) {
        int pixel = image.getRGB(x, y);
        return pixel >> 24 == 0x00 || ((pixel & 0x00FFFFFF) == 0);
    }

    public static void unzipSkinPack(String url) throws IOException {
        File tempZip = new File(SKIN_DIRECTORY + "/temp/" + System.currentTimeMillis() + ".zip");
        Regeneration.LOG.info("Downloading " + url + " to " + tempZip.getAbsolutePath());
        FileUtils.copyURLToFile(new URL(url), tempZip);
        try (ZipFile file = new ZipFile(tempZip)) {
            FileSystem fileSystem = FileSystems.getDefault();
            Enumeration< ? extends ZipEntry > entries = file.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    Files.createDirectories(fileSystem.getPath(SKIN_DIRECTORY + File.separator + entry.getName()));
                } else {
                    InputStream is = file.getInputStream(entry);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    String uncompressedFileName = SKIN_DIRECTORY + File.separator + entry.getName();
                    Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
                    Regeneration.LOG.info("Extracting file: " + uncompressedFilePath);
                    File temp = uncompressedFilePath.toFile();
                    if (temp.exists()) {
                        Regeneration.LOG.info("Recreating: " + uncompressedFilePath);
                        temp.delete();
                    }
                    Files.createFile(uncompressedFilePath);
                    FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);
                    while (bis.available() > 0) {
                        fileOutput.write(bis.read());
                    }
                    fileOutput.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tempZip.exists()) {
            FileUtils.forceDelete(tempZip.getParentFile());
        }
    }

    public static List< File > listAllSkins(PlayerUtil.SkinType choices) {
        File directory = null;
        switch (choices) {
            case EITHER:
                directory = SKIN_DIRECTORY;
                break;
            case ALEX:
                directory = SKIN_DIRECTORY_ALEX;
                break;
            case STEVE:
                directory = SKIN_DIRECTORY_STEVE;
                break;
        }
        Collection< File > folderFiles = FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        folderFiles.removeIf(file -> !file.getName().endsWith(".png"));
        return new ArrayList<>(folderFiles);
    }

    public static void downloadTrendingSkins() throws IOException {
        if (!RegenConfig.SKIN.downloadTrendingSkins.get() || !RegenUtil.doesHaveInternet()) return;
        File trendingDir = TRENDING_ALEX;
        if (!trendingDir.exists()) {
            if (trendingDir.mkdirs()) {
                Regeneration.LOG.info("Creating Directory: " + trendingDir);
                Regeneration.LOG.info("Creating Directory: " + TRENDING_ALEX);
                Regeneration.LOG.info("Creating Directory: " + TRENDING_STEVE);
            }
        }
        long attr = trendingDir.lastModified();
        if (System.currentTimeMillis() - attr >= 86400000 || Objects.requireNonNull(trendingDir.list()).length == 0) {
            FileUtils.cleanDirectory(trendingDir);
            Regeneration.LOG.warn("Refreshing Trending skins");
            for (String skin : getSkins("https://namemc.com/minecraft-skins")) {
                String cleanName = skin.replaceAll("https://namemc.com/texture/", "").replaceAll(".png", "");
                downloadSkins(new URL(skin), "trending_" + cleanName, TRENDING_ALEX, TRENDING_STEVE);
            }
        }
    }

    public static void downloadTimelord() throws IOException {
        if (!SKIN_DIRECTORY_FEMALE.exists()) {
            if (SKIN_DIRECTORY_FEMALE.mkdirs()) {
                Regeneration.LOG.info("Creating Directory: " + SKIN_DIRECTORY_FEMALE);
                Regeneration.LOG.info("Creating Directory: " + SKIN_DIRECTORY_MALE);
            }
        }
        if (!SKIN_DIRECTORY_MALE.exists()) {
            if (SKIN_DIRECTORY_MALE.mkdirs()) {
                Regeneration.LOG.info("Creating Directory: " + SKIN_DIRECTORY_MALE);
            }
        }

        long attr = SKIN_DIRECTORY_MALE.lastModified();
        if (System.currentTimeMillis() - attr >= 86400000 || Objects.requireNonNull(SKIN_DIRECTORY_MALE.list()).length == 0) {
            FileUtils.cleanDirectory(SKIN_DIRECTORY_FEMALE);
            FileUtils.cleanDirectory(SKIN_DIRECTORY_MALE);
            Regeneration.LOG.warn("Refreshing Timelord skins");

            String[] genders = new String[]{"male", "female"};
            for (String gender : genders) {
                for (String skin : getSkins("https://namemc.com/minecraft-skins/tag/" + gender)) {
                    String cleanName = skin.replaceAll("https://namemc.com/texture/", "").replaceAll(".png", "");
                    downloadSkinsSpecific(new URL(skin), "timelord_" + gender + "_" + cleanName, gender.equals("male") ? SKIN_DIRECTORY_MALE : SKIN_DIRECTORY_FEMALE);
                }
            }
        }
    }
}
