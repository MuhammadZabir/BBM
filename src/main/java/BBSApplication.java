import action.BlumBlumShub;
import entity.Storage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BBSApplication {

    public static void main(String args[]) {
        StringBuffer content = new StringBuffer();
        List<Storage> storages = new ArrayList<>();
        try {
            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                    ": Searching the key generator DES" + System.lineSeparator());
            KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                    ": Found the key generator" + System.lineSeparator());
            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                    ": Generating secret key" + System.lineSeparator());
            SecretKey myDesKey = keygenerator.generateKey();
            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                    ": Secret key generated" + System.lineSeparator());

            Cipher encCipher;
            Cipher decCipher;

            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                    ": Initializing cipher for encryption" + System.lineSeparator());
            encCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                    ": Initializing cipher for decryption" + System.lineSeparator());
            decCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            encCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                    ": Success at creating cipher for encryption" + System.lineSeparator());
            decCipher.init(Cipher.DECRYPT_MODE, myDesKey);
            content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                    ": Success at creating cipher for decryption" + System.lineSeparator());

            for (int x = 0; x < 64; x++) {
                Storage storage = new Storage();
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                        ": Creating random text uses BlumBlumShub" + System.lineSeparator());
                List<String> allInt = BlumBlumShub.randomText(content);
                StringBuffer randomText = new StringBuffer();
                allInt.forEach(integer -> randomText.append(integer));
                StringBuffer randomTextBinary = new StringBuffer();
                allInt.forEach(integer -> randomTextBinary.append(new BigInteger(integer, 16).toString(2) + ","));
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                        ": Done creating random text" + System.lineSeparator());
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                        ": Random Text = '" + randomText.toString() + "'" + System.lineSeparator());
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                        ": Random Text [Binary] = '" + randomTextBinary.toString().replace(",", "") + "'" + System.lineSeparator());
                byte[] text = randomTextBinary.toString().getBytes();

                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                        ": Encrypting the random text" + System.lineSeparator());
                byte[] textEncrypted = encCipher.doFinal(text);
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                        ": Encryption success" + System.lineSeparator());
                String cipherTextBinary = toBinary(textEncrypted);
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                        ": Encrypted = '" + textEncrypted + "'" + System.lineSeparator());
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                        ": Encrypted [Binary] = '" + cipherTextBinary + "'" + System.lineSeparator());
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                        ": Decrypting the encrypted text" + System.lineSeparator());
                byte[] textDecrypted = decCipher.doFinal(textEncrypted);
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                        ": Decryption success" + System.lineSeparator());
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                        ": Decrypted [Binary] = '" + new String(textDecrypted).replace(",", "") + "'" + System.lineSeparator());
                String[] binaryParts = new String(textDecrypted).split(",");
                StringBuffer decryptedText = new StringBuffer();
                for (String s : binaryParts) {
                    decryptedText.append(Long.toHexString(Long.parseLong(s, 2)));
                }
                content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                        ": Decrypted = '" + decryptedText.toString() + "'" + System.lineSeparator());
                storage.setHexaOri(randomText.toString());
                storage.setBinaryOri(randomTextBinary.toString().replace(",", ""));
                storage.setCipherOri(textEncrypted.toString());
                storage.setBinaryCipher(cipherTextBinary.toString());
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
        Cell bitErrModifyHdr = header.createCell(colNum++);
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
            cipherModify.setCellValue("");
            Cell binaryModify = row.createCell(colNum++);
            binaryModify.setCellValue("");
            Cell bitErr = row.createCell(colNum++);
            bitErr.setCellValue("");
            Cell bitErrModify = row.createCell(colNum++);
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
