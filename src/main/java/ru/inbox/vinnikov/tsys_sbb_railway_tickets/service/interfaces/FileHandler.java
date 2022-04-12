package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.interfaces;

import java.io.*;
import java.time.LocalDateTime;
import static java.lang.Thread.sleep;
import static ru.inbox.vinnikov.tsys_sbb_railway_tickets.TsysSbbRailwayTicketsApplication.LOGGER;

public interface FileHandler {
    public default void writeInOutputFile(String text,String fileName)
    {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,true));
            sleep(200);
            writer.write(text);
            sleep(200);
            writer.flush();
            sleep(1_000);
            writer.close();
            sleep(200);
        } catch (IOException | InterruptedException e) {
            LOGGER.error("interface FileHandler -writeInOutputFile--catch-> " + LocalDateTime.now() + "\n" + e);;
        }
    }

    // TODO настроить, так как ни разу не использовался
    public default int readFmFile(String fileName)
    {
        int digit = 0;
        try
        {
            // считать из файла число
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            while (reader.ready())
            {
                String file = reader.readLine();
                digit = Integer.parseInt(file);
            }
            reader.close();
        } catch (IOException ie)
        {
            LOGGER.error("interface FileHandler -readFmFile--catch-> " + LocalDateTime.now() + "\n" + ie);
        }
        return digit;
    }


}
