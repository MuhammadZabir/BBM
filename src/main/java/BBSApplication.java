import action.BlumBlumShub;
import entity.Storage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BBSApplication {

    public static void main(String args[]) {
        StringBuffer content = new StringBuffer();
        List<Storage> storages = new ArrayList<>();
        try {
            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                    .append(": Searching the key generator DES").append(System.lineSeparator());
            KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
            KeyGenerator keyGeneratorMod = KeyGenerator.getInstance("DESede");
            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                    .append(": Found the key generator").append(System.lineSeparator());
            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                    .append(": Generating secret key").append(System.lineSeparator());
            SecretKey myDesKey = keygenerator.generateKey();
            SecretKey myDesEdeKey = keyGeneratorMod.generateKey();
            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                    .append(": Secret key generated").append(System.lineSeparator());

            Cipher encCipher;
            Cipher decCipher;
            Cipher encCipherMod;
            Cipher decCipherMod;

            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                    .append(": Initializing cipher for encryption").append(System.lineSeparator());
            encCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            encCipherMod = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                    .append(": Initializing cipher for decryption").append(System.lineSeparator());
            decCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            decCipherMod = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            encCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
            encCipherMod.init(Cipher.ENCRYPT_MODE, myDesEdeKey);
            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                    .append(": Success at creating cipher for encryption").append(System.lineSeparator());
            decCipher.init(Cipher.DECRYPT_MODE, myDesKey);
            decCipherMod.init(Cipher.DECRYPT_MODE, myDesEdeKey);
            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                    .append(": Success at creating cipher for decryption").append(System.lineSeparator());

            for (int x = 0; x < 64; x++) {
                Storage storage = new Storage();
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                        .append(": Creating random text uses BlumBlumShub").append(System.lineSeparator());
                List<String> allInt = BlumBlumShub.randomText(content);
                StringBuilder randomText = new StringBuilder();
                allInt.forEach(randomText::append);
                StringBuffer randomTextBinary = new StringBuffer();
                allInt.forEach(integer -> randomTextBinary.append(new BigInteger(integer, 16).toString(2))
                        .append(","));
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                        .append(": Done creating random text").append(System.lineSeparator());
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                        .append(": Random Text = '").append(randomText.toString()).append("'").append(System.lineSeparator());
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                        .append(": Random Text [Binary] = '").append(randomTextBinary.toString().replace(",", "")).append("'").append(System.lineSeparator());
                byte[] text = randomTextBinary.toString().getBytes();

                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                        .append(": Encrypting the random text").append(System.lineSeparator());
                byte[] textEncrypted = encCipher.doFinal(text);
                byte[] textEncryptedMod = encCipherMod.doFinal(text);
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                        .append(": Encryption success").append(System.lineSeparator());
                String cipherTextBinary = toBinary(textEncrypted);
                String cipherTextBinaryMod = toBinary(textEncryptedMod);
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                        .append(": Encrypted = '").append(Arrays.toString(textEncrypted)).append("'").append(System.lineSeparator());
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                        .append(": Encrypted [Binary] = '").append(cipherTextBinary).append("'").append(System.lineSeparator());
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                        .append(": Decrypting the encrypted text").append(System.lineSeparator());
                byte[] textDecrypted = decCipher.doFinal(textEncrypted);
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                        .append(": Decryption success").append(System.lineSeparator());
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                        .append(": Decrypted [Binary] = '").append(new String(textDecrypted).replace(",", ""))
                        .append("'").append(System.lineSeparator());
                String[] binaryParts = new String(textDecrypted).split(",");
                StringBuilder decryptedText = new StringBuilder();
                for (String s : binaryParts) {
                    decryptedText.append(Long.toHexString(Long.parseLong(s, 2)));
                }
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")))
                        .append(": Decrypted = '").append(decryptedText.toString()).append("'").append(System.lineSeparator());
                storage.setHexaOri(randomText.toString());
                storage.setBinaryOri(randomTextBinary.toString().replace(",", ""));
                storage.setCipherOri(textEncrypted.toString());
                storage.setBinaryCipher(cipherTextBinary);
                storage.setCipherModify(textEncryptedMod.toString());
                storage.setBinaryModify(cipherTextBinaryMod);
                storages.add(storage);
            }

            generatingExcel(storages);

            Files.write(Paths.get(System.getProperty("user.dir"), "Result.txt"), content.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static void generatingExcel(List<Storage> storages) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Cipher");

        int rowNum = 0;
        Row header = sheet.createRow(rowNum++);

        int colNum = 0;
        Cell hexaOriHdr = header.createCell(colNum++);
        hexaOriHdr.setCellValue("Hexadecimal Text");
        Cell binaryOriHdr = header.createCell(colNum++);
        binaryOriHdr.setCellValue("Binary Text");
        Cell cipherOriHdr = header.createCell(colNum++);
        cipherOriHdr.setCellValue("Cipher Text");
        Cell binaryCipherHdr = header.createCell(colNum++);
        binaryCipherHdr.setCellValue("Binary Cipher Text");
        Cell cipherModifyHdr = header.createCell(colNum++);
        cipherModifyHdr.setCellValue("Cipher Modify Text");
        Cell binaryModifyHdr = header.createCell(colNum++);
        binaryModifyHdr.setCellValue("Binary Modify Text");
        Cell bitErrHdr = header.createCell(colNum++);
        bitErrHdr.setCellValue("Bit Error");
        Cell bitErrModifyHdr = header.createCell(colNum);
        bitErrModifyHdr.setCellValue("Bit Modify Error");

        for (Storage storage : storages) {
            Row row = sheet.createRow(rowNum++);
            colNum = 0;
            Cell hexaOri = row.createCell(colNum++);
            hexaOri.setCellValue(storage.getHexaOri());
            Cell binaryOri = row.createCell(colNum++);
            binaryOri.setCellValue(storage.getBinaryOri());
            Cell cipherOri = row.createCell(colNum++);
            cipherOri.setCellValue(storage.getCipherOri());
            Cell binaryCipher = row.createCell(colNum++);
            binaryCipher.setCellValue(storage.getBinaryCipher());
            Cell cipherModify = row.createCell(colNum++);
            cipherModify.setCellValue(storage.getCipherModify());
            Cell binaryModify = row.createCell(colNum++);
            binaryModify.setCellValue(storage.getBinaryModify());
            Cell bitErr = row.createCell(colNum++);
            bitErr.setCellValue("");
            Cell bitErrModify = row.createCell(colNum);
            bitErrModify.setCellValue("");
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(Paths.get(System.getProperty("user.dir"), "Cipher.xlsx").toString());
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static String toBinary( byte[] bytes ) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }

}
