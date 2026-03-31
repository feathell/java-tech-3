package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

@WebServlet("/files")
public class FilesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getParameter("path");
        File current = (path == null || path.isBlank())
                ? new File(System.getProperty("user.home"))
                : new File(path);

        if (!current.exists() || !current.isDirectory()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong path");
            return;
        }

        File[] items = current.listFiles();
        if (items == null) items = new File[0];
        Arrays.sort(items, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));

        req.setAttribute("generatedAt", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()));
        req.setAttribute("currentPath", current.getAbsolutePath());
        req.setAttribute("parentPath", current.getParent());
        req.setAttribute("items", items);

        req.getRequestDispatcher("/WEB-INF/files.jsp").forward(req, resp);
    }
}
