package csc.Controllers;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

public class FileUploadServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setStatus(200); // Status: OK
        resp.setContentType("text/plain");

        FileWriter writer = null;
        String fileKey = null;

        try {

            ServletFileUpload fileUpload = new ServletFileUpload();
            FileItemIterator items = fileUpload.getItemIterator(req);

            while (items.hasNext()) {
                FileItemStream item = items.next();

                if (item.isFormField())
                    continue;

                String fileName = FilenameUtils.getName(item.getName());
                InputStream fileContent = item.openStream();

                int fileSize = fileContent.available();

                resp.getWriter().println("FileName: " + fileName);
                resp.getWriter().println("ContentType: " + item.getContentType());
                resp.getWriter().println();

                if (fileSize > 0)
                {
                    if (writer == null)
                        writer = new FileWriter(item.getContentType());

                    byte[] buffer = new byte[2048];
                    int count;
                    while ((count = fileContent.read(buffer)) > 0)
                    {
                        writer.write(buffer, 0, count);
                    }
                }

                fileContent.close();
            }
        } catch (FileUploadException e) {
            throw new IOException("Cannot parse multipart request.", e);
        }

        if (writer != null)
        {
            fileKey = writer.fileKey();
            resp.getWriter().println("Stored " + writer.size() + " bytes into file [" +  fileKey + "].");
            writer.close();
        }

        if (fileKey != null)
        {
            //TODO: Respond appropriately!
            // resp.setStatus(200); // Status: OK
            // resp.setContentType("text/plain");
            // resp.getWriter().println("Uploaded file '" + fileName + "' with key: " + fileKey);

            resp.getWriter().println();
            resp.getWriter().println("Trying to retrieve the file...");

            //test reading back the file
            String result = FileStorage.retrieveImage(fileKey, resp.getWriter());
            resp.getWriter().println("FileStorage.retrieveImage returned: " +  result);
        }
        else
        {
            resp.getWriter().println("The file key is null!");

            // resp.setStatus(406); // Status: Not Acceptable
            // resp.setStatus(400); // Status: Bad Request
        }
    }
}
