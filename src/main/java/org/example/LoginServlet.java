package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userLogin") != null) {
            resp.sendRedirect(req.getContextPath() + "/files");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        if (AuthService.authenticate(login, password)) {
            HttpSession session = req.getSession(true);
            session.setAttribute("userLogin", login.trim());
            resp.sendRedirect(req.getContextPath() + "/files");
            return;
        }

        req.setAttribute("error", "Неверный логин или пароль");
        String loginValue = "";
        if (login != null) {
            loginValue = login.trim();
        }
        req.setAttribute("login", loginValue);
        req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
    }
}
