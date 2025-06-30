import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import bean.CategoryBean;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * @author kousu
 */
public class NewProduct extends HttpServlet {
    /** デフォルトシリアルバージョンID.(シリアライズ) */
    private static final long serialVersionUID = 1L;

    String messageOfNameCheck = null;

    /**
     *データベースから取得した情報を保持して、"JSP"に遷移するGetメソッド。
     */
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // 文字コードの指定
        response.setContentType("text/plain; charset=UTF-8");
        // 動的配列を作成
        List<CategoryBean> categoryList = new ArrayList<CategoryBean>();
        // 最新のIDをdoGetのスコープで定義
        int newId = 0;

        try {
            // データベースに接続
            InitialContext ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup("java:/comp/env/jdbc");
            Connection con = ds.getConnection();
            // SQL文を構築
            String sqlCategory = "SELECT A.CATEGORYID AS カテゴリーID , A.CATEGORYNAME AS カテゴリー名　"
                    + "FROM CATEGORIES A JOIN PRODUCTS B ON A.CATEGORYID = B.CATEGORYID "
                    + "GROUP BY A.CATEGORYID,A.CATEGORYNAME ORDER BY A.CATEGORYID";
            // SQL文を実行
            PreparedStatement stCategory = con.prepareStatement(sqlCategory);
            ResultSet rsCategory = stCategory.executeQuery();
            // 実行結果を格納
            while (rsCategory.next()) {
                CategoryBean categoryBean = new CategoryBean();
                // SQLで取得した情報をセットしていく
                categoryBean.setCategoryID(rsCategory.getInt("カテゴリーID"));
                categoryBean.setCategoryName(rsCategory.getString("カテゴリー名"));
                // 配列に追加
                categoryList.add(categoryBean);
            }
            // SQL文を構築
            String sqlNewId = "SELECT MAX(PRODUCTID)+1 AS 最新ID " + "FROM PRODUCTS "
                    + "ORDER BY CATEGORYID";
            // SQL文を実行
            PreparedStatement stNewId = con.prepareStatement(sqlNewId);
            ResultSet rsNewId = stNewId.executeQuery();
            // 実行結果を格納
            while (rsNewId.next()) {
                newId = (rsNewId.getInt("最新ID"));
            }
            // データベースの切断処理
            stCategory.close();
            con.close();
        } catch (Exception e) {
            // エラー処理
            e.printStackTrace();
        }
        HttpSession session = request.getSession();
        // リクエストスコープに保存
        session.setAttribute("ctgList", categoryList);
        session.setAttribute("newId", newId);
        // JSPファイルにリクエストスコープに保存したデータを送信
        RequestDispatcher rd = request.getRequestDispatcher("newProduct.jsp");
        rd.forward(request, response);
    }

    /**
     *  フォームからデータを取得して、登録するメソッド。
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // 成功メッセージの変数
        String succeed = null;
        // formからのパラメーターの受け取り
        String productName = null;
        productName = request.getParameter("productName");
        String categoryName = null;
        categoryName = request.getParameter("CategoryName");
        String productId = request.getParameter("productID");
        String productCode = request.getParameter("productCode");
        String Price = request.getParameter("Price");
        String selectedCategoryId = request.getParameter("category");
        // エラー処理用の配列・変数
        List<String> errorMessage = new ArrayList<String>();
        boolean check = true;
        // 入力チェック
        check &= checkText(productCode, "商品コード", errorMessage);
        check &= checkText(Price, "価格", errorMessage);
        // 入力チェックに引っかかった場合にその時点でエラーメッセージとデータを返す
        if (!check) {
            // リクエストスコープに保存
            request.setAttribute("succeed", succeed);
            request.setAttribute("errorMessage", errorMessage);
            request.setAttribute("productCode", productCode);
            request.setAttribute("productName", productName);
            request.setAttribute("Price", Price);
            request.setAttribute("selectedCategoryID", selectedCategoryId);
            request.setAttribute("CategoryName", categoryName);
            // JSPファイルに転送
            RequestDispatcher rd = request.getRequestDispatcher(
                    "newProduct.jsp");
            rd.forward(request, response);
            return;
        }

        try {
            // データベース接続
            InitialContext ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup("java:/comp/env/jdbc");
            Connection con = ds.getConnection();
            // SQL文構築
            String sqlInsert = "INSERT INTO Products "
                    + " (PRODUCTID,PRODUCTCODE,PRODUCTNAME,PRICE,CATEGORYID)"
                    + " VALUES(?,?,?,?,?)";

            // SQLの実行用にSQL文に設定
            PreparedStatement st = con.prepareStatement(sqlInsert);
            st.setInt(1, Integer.parseInt(productId));
            st.setInt(2, Integer.parseInt(productCode));
            st.setString(3, productName);
            st.setInt(4, Integer.parseInt(Price));
            st.setInt(5, Integer.parseInt(selectedCategoryId));
            st.executeUpdate();
            succeed = "登録完了しました";
            // データベースの切断処理
            st.close();
            con.close();
        } catch (Exception e) {
            // エラー処理
            e.printStackTrace();
        }
        // リクエストスコープに保存
        request.setAttribute("succeed", succeed);
        request.setAttribute("errorMessage", errorMessage);
        
        request.setAttribute("productCode", productCode);
        request.setAttribute("productName", productName);
        request.setAttribute("Price", Price);
        request.setAttribute("selectedCategoryID", selectedCategoryId);
        request.setAttribute("CategoryName", categoryName);
        // JSPファイルに転送
        RequestDispatcher rd = request.getRequestDispatcher("newProduct.jsp");
        rd.forward(request, response);
    }

    // 入力チェックメソッド
    private boolean checkText(String code, String fieldName,
            List<String> errors) {
        // nullチェック
        if (code == null) {
            errors.add(fieldName + "が入力されていません");
            return false;
        }
        // 空欄チェック
        if (code.trim().isEmpty()) {
            errors.add(fieldName + "が入力されていません。");
            return false;
        }
        // 文字列かのチェック
        try {
            Integer.parseInt(code);
        } catch (NumberFormatException e) {
            errors.add(fieldName + " は数値で入力してください");
            return false;
        }
        return true;
    }

}