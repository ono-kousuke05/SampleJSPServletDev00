import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import bean.EmployeeBean;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class Sales extends HttpServlet {
    /** デフォルトシリアルバージョンID.(シリアライズ) */
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // 文字コードの指定
        response.setContentType("text/plain; charset=UTF-8");
        // 表で使用するデータを格納するリストの定義
        List<EmployeeBean> salesList = new ArrayList<EmployeeBean>();
        try {
            // データベースに接続
            InitialContext ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup("java:/comp/env/jdbc");
            Connection con = ds.getConnection();
            // SQL文を構築
            String sql = "SELECT " + "A.EMPLOYEENAME　AS 従業員名 "
                    + ",COALESCE(SUM(B.QUANTITY * C.PRICE),0) AS 売上金額 "
                    + "FROM EMPLOYEES A " + "LEFT JOIN  SALES B "
                    + "ON A.EMPLOYEEID = B.EMPLOYEEID "
                    + "LEFT JOIN PRODUCTS C " + "ON B.PRODUCTID = C.PRODUCTID "
                    + "GROUP BY A.EMPLOYEENAME,A.EMPLOYEEID " + "ORDER BY "
                    + " A.EMPLOYEEID ";
            // SQL文の実行
            PreparedStatement st = con.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            // SQL文のデータを格納
            while (rs.next()) {
                EmployeeBean employeeBean = new EmployeeBean();
                employeeBean.setEmployeeName(rs.getString("従業員名"));
                employeeBean.setSalesSum(rs.getInt("売上金額"));
                salesList.add(employeeBean);
            }
            st.close();
            con.close();
        } catch (Exception e) {
            // エラー処理
            e.printStackTrace();
        }
        // リクエストスコープ/セッションスコープに保存し、JSPファイルへ受け渡し
        request.setAttribute("SalesList", salesList);
        HttpSession session = request.getSession();
        session.setAttribute("SaleList", salesList);
        RequestDispatcher rd = request.getRequestDispatcher("sales.jsp");
        rd.forward(request, response);
    }
}
