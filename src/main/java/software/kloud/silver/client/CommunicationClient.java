package software.kloud.silver.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import software.kloud.sc.SilverCommunication;
import software.kloud.sc.StatusResponseDTO;
import software.kloud.sc.TransferDTO;

import java.io.IOException;
import java.util.Optional;
import java.util.zip.CRC32;

public class CommunicationClient {
    private static final ObjectMapper objMapper = new ObjectMapper();
    // TODO do not init here
    private final HttpTransfer transfer;

    public CommunicationClient(String url, String key) {
        this.transfer = new HttpTransfer(url, key);
    }

    public <T extends SilverCommunication> Optional<T> get(String key, Class<? extends T> type) throws IOException {
        T entity;

        try {
            var json = transfer.get(key, type.getCanonicalName());
            entity = objMapper.readValue(json, type);
        } catch (IOException e) {
            throw new IOException("Failed to get from silver", e);
        }

        return Optional.ofNullable(entity);
    }

    public <T extends SilverCommunication> StatusResponseDTO save(T obj) throws IOException {
        var clazz = obj.getClass();
        var payload = objMapper.writeValueAsString(obj);

        var crc32Gen = new CRC32();
        crc32Gen.update(payload.getBytes());
        var checksum = crc32Gen.getValue();

        var transferDTO = new TransferDTO(payload, checksum, clazz);
        var jsonPayload = objMapper.writeValueAsString(transferDTO);

        try {
            String json = transfer.post(jsonPayload);
            return objMapper.readValue(json, StatusResponseDTO.class);
        } catch (IOException e) {
            throw new IOException("Failed to post to silver spork", e);
        }
    }
}
