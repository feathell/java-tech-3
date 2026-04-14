package org.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {"", "/"})
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        Object login = null;
        if (session != null) {
            login = session.getAttribute("userLogin");
        }

        if (login == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
        } else {
            resp.sendRedirect(req.getContextPath() + "/files");
        }
    }
}
