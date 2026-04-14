package org.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        String login = null;
        if (session != null) {
            login = (String) session.getAttribute("userLogin");
        }
        if (login == null || login.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getParameter("path");
        if (path == null || path.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Path is required");
            return;
        }

        File file = AuthService.resolveInsideHome(login, path);
        if (file == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }
        if (!file.exists() || !file.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }

        String mime = getServletContext().getMimeType(file.getName());
        if (mime == null) mime = "application/octet-stream";

        resp.setContentType(mime);
        resp.setContentLengthLong(file.length());
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName().replace("\"", "") + "\"");

        try (FileInputStream in = new FileInputStream(file)) {
            in.transferTo(resp.getOutputStream());
        }
    }
}
