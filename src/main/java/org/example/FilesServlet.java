package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
        File home = AuthService.userHome(login);
        File current = AuthService.resolveInsideHome(login, path);
        if (current == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }
        if (!current.exists() || !current.isDirectory()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong path");
            return;
        }

        File[] items = current.listFiles();
        if (items == null) items = new File[0];
        Arrays.sort(items, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));

        String parentPath = null;
        File parent = current.getParentFile();
        if (parent != null && AuthService.isInsideHome(home, parent)) {
            parentPath = parent.getAbsolutePath();
        }

        req.setAttribute("generatedAt", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()));
        req.setAttribute("currentPath", current.getAbsolutePath());
        req.setAttribute("parentPath", parentPath);
        req.setAttribute("userLogin", login);
        req.setAttribute("items", items);

        req.getRequestDispatcher("/WEB-INF/files.jsp").forward(req, resp);
    }
}
