package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userLogin") != null) {
            resp.sendRedirect(req.getContextPath() + "/files");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        String error = AuthService.register(login, password, email);
        if (error != null) {
            String loginValue = "";
            if (login != null) {
                loginValue = login.trim();
            }
            String emailValue = "";
            if (email != null) {
                emailValue = email.trim();
            }

            req.setAttribute("error", error);
            req.setAttribute("login", loginValue);
            req.setAttribute("email", emailValue);
            req.getRequestDispatcher("/WEB-INF/register.jsp").forward(req, resp);
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("userLogin", login.trim());
        resp.sendRedirect(req.getContextPath() + "/files");
    }
}
