package hello.jdbc.repository;


import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    //회원 저장
    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values(?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch(SQLException e){
            log.error("db error", e);
            throw e;
        } finally {
            //리소스 정리, 자원을 다 쓰고 직접 닫아줘야 함
            close(con, pstmt, null);
        }

    }


    //회원 조회
    public Member findById(String memberId) throws SQLException{
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();

            if(rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }


        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }


    }


    //회원 수정
    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2,  memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch(SQLException e){
            log.error("db error", e);
            throw e;
        } finally {
            //리소스 정리, 자원을 다 쓰고 직접 닫아줘야 함
            close(con, pstmt, null);
        }

    }



    //회원 삭제
    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch(SQLException e){
            log.error("db error", e);
            throw e;
        } finally {
            //리소스 정리, 자원을 다 쓰고 직접 닫아줘야 함
            close(con, pstmt, null);
        }

    }

    private void close(Connection con, Statement stmt, ResultSet rs){
        if(rs != null){
            try {
                rs.close();
            }catch(SQLException e){
                log.info("error",e);
            }
        }

        if(stmt != null){
            try {
                stmt.close();
            }catch(SQLException e){
                log.info("error",e);
            }
        }

        if(con != null){
            try {
                con.close();
            }catch(SQLException e){
                log.info("error",e);
            }
        }
    }

    private static Connection getConnection() throws SQLException {

        //DriverManager 라는 구체화에 의존하는 코드
        Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        return con;
        //return DBConnectionUtil.getConnection();
    }
}