package DiaLogServlet;

import DiaLogApp.*;
import DiaLogSQL.UserLoginDataSQL;
import com.google.gson.Gson;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@WebServlet(urlPatterns={"/home", "/register", "/UserDataTesting","/Admin"}, loadOnStartup=1)
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        String servletPath = req.getServletPath();
        UserLoginDataSQL.createTable();
        switch (servletPath) {
            case "/register":
                forwardTo(req, resp, "/register.html");
                break;
            case "/home":
                resp.getWriter().write("Welcome to the home page! ");
                break;
            case "/UserDataTesting":
                resp.getWriter().write("UserLoginData Display for testing purpose");
                UserLoginDataSQL.displayData(resp);
                break;
            case "/Admin":
                UserLoginDataSQL.insertData("Admin","1234567890");

            default:
                resp.getWriter().write("404 Not Found");
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String servletPath = req.getServletPath();

        switch (servletPath) {
            case "/register":
                String jsonData2 = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
                Gson gson2 = new Gson();
                UserRegisterData user2 = gson2.fromJson(jsonData2, UserRegisterData.class);

                String userAccountRegister = user2.getUserAccount();
                String userPasswordRegister = user2.getUserPassword();
                String userConfirmedPassword = user2.getUserConfirmedPassword();

                try {
                    if (UserLoginDataSQL.checkIdentity(userAccountRegister,userPasswordRegister)==0){
                        UserLoginDataSQL.insertData(userAccountRegister,userPasswordRegister);
                        sendSuccessResponse(resp);
                    }
                    else{
                        sendErrorResponse(resp);
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        }

    }

    private void forwardTo(HttpServletRequest req, HttpServletResponse resp, String path) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher(path);
        dispatcher.forward(req, resp);
    }

    private void sendSuccessResponse(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(new Gson().toJson(new ResponseObject(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage())));

    }
    private void sendErrorResponse(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(new Gson().toJson(new ResponseObject(ErrorCode.NO_AUTH_ERROR.getCode(), ErrorCode.NO_AUTH_ERROR.getMessage())));
    }
}


// http://localhost:8080/dialog/login
// http://localhost:8080/dialog/register

//Heroku
//https://dialog-1d1125195912.herokuapp.com/home
//https://dialog-1d1125195912.herokuapp.com/login
//https://dialog-1d1125195912.herokuapp.com/register

