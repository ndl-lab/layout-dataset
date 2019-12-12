package jp.go.ndl.lab.annotation.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;


import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
public class RESTUtils {


    /**
     * nullまたは空文字が含まれているオブジェクトが存在するか判定する。
     *
     * @param inputs オブジェクト
     * @return 判定結果
     */
    public static boolean blankCheck(Object... inputs) {
        for (Object input : inputs) {
            if (input == null || StringUtils.isBlank(input.toString()))
                return true;
        }
        return false;
    }

    /**
     * nullまたは空文字以外が含まれているオブジェクトが存在するかを判定する。
     * @param inputs オブジェクト
     * @return 判定結果
     */
    public static boolean isAllBlank(Object... inputs) {
        for (Object input : inputs) {
            if (input != null && !StringUtils.isBlank(input.toString()))
                return false;
        }
        return true;
    }

    /**
     * コレクションがnullまたは空文字でないかを判定する。
     *
     * @param c コレクション
     * @return 判定結果
     */
    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    /**
     * 指定されたファイルからHTTP接続情報のheader情報を格納する。
     *
     * @param fileName ファイル名
     * @param path パス
     * @return HTTP接続情報
     */
    public static HttpEntity<Resource> fileDownloadResponse(String fileName, Path path) {
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Transfer-Encoding", "binary");
        try {
            // ファイル名をエンコード（半角スペースはエンコードしない）
            String[] split = fileName.split("");
            for (int i = 0; i < split.length; i++) {
                String str = split[i];
                if (!str.matches(" ")) {
                    split[i] = URLEncoder.encode(str, StandardCharsets.UTF_8.name());
                }
            }
            String encodeFileName = String.join("", split);
            header.add("Content-disposition", "attachment;filename*=UTF-8''" + encodeFileName);
        } catch (UnsupportedEncodingException ex) {
        }
        return new HttpEntity<>(new FileSystemResource(path.toFile()), header);
    }

    /**
     * 指定されたファイルを基にheader情報を追加し、streamingResponseBody部を返却する。<br>
     * 大量のダウンロードを取得する際にRequest毎にレンダリングするクラス
     *
     * @param fileName ファイル名
     * @param streamingResponseBody アウトプットストリーム
     * @return ResponseEntity<> レスポンス情報
     */
    public static ResponseEntity<StreamingResponseBody> streamDownloadResponse(String fileName, StreamingResponseBody streamingResponseBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("Content-Disposition", "attachment; filename=" + fileName);
        headers.add("Content-Transfer-Encoding", "binary");
        return new ResponseEntity<>(streamingResponseBody, headers, HttpStatus.OK);
    }

    /**
     * メソッド名を取得する。
     *
     * @return メソッド名
     */
    public static String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

}
