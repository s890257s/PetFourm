package tw.com.eeit.pawposter.model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tw.com.eeit.pawposter.model.bean.MemberPetLike;
import tw.com.eeit.pawposter.util.DateTool;

public class MemberPetLikeDao {

	private Connection conn;

	/**
	 * 沒有「無參數的建構子(無參建購子)」，代表使用 new 建立此物件時，一定要傳入參數 conn。
	 * 
	 * @param conn 外部傳入的連線物件，DAO 不實作連線
	 */
	public MemberPetLikeDao(Connection conn) {
		this.conn = conn;
	}

	/* === Read === */

	/**
	 * 根據 member_id 尋找其所有按讚紀錄。
	 * 
	 * @param memberId 會員Id。
	 * @return List<MemberPetLike> 按讚紀錄的集合。
	 */
	public List<MemberPetLike> findAllMemberPetLikeByMemberId(Integer memberId) throws SQLException {
		final String SQL = "SELECT * FROM [paw_poster].[dbo].[member_pet_like] WHERE [member_id] = ?";
		PreparedStatement ps = conn.prepareStatement(SQL);
		ps.setInt(1, memberId);

		ResultSet rs = ps.executeQuery();

		List<MemberPetLike> memberPetLikes = new ArrayList<>();
		while (rs.next()) {
			MemberPetLike memberPetLike = new MemberPetLike();
			memberPetLike.setLikeId(rs.getInt("like_id"));
			memberPetLike.setCreateDate(rs.getDate("create_date"));
			memberPetLike.setMember(rs.getInt("member_id"));
			memberPetLike.setPet(rs.getInt("pet_id"));

			memberPetLikes.add(memberPetLike);
		}

		rs.close();
		ps.close();
		return memberPetLikes;
	}

	/**
	 * 根據 member_id 與 pet_id 尋找會員的單筆按讚紀錄。
	 * 
	 * @param memberPetLike 按讚的物件。
	 * @return true: member 按了 pet 讚，false: member 沒按 pet 讚
	 */
	public Boolean isLikeExist(MemberPetLike memberPetLike) throws SQLException {
		final String SQL = "SELECT * FROM [paw_poster].[dbo].[member_pet_like] WHERE [member_id] = ? AND [pet_id] = ?";
		PreparedStatement ps = conn.prepareStatement(SQL);
		ps.setInt(1, memberPetLike.getMember().getMemberId());
		ps.setInt(2, memberPetLike.getPet().getPetId());

		ResultSet rs = ps.executeQuery();

		boolean isLikeExist = rs.next();

		rs.close();
		ps.close();

		return isLikeExist;
	}

	/**
	 * 計算 member_pet_like 表格的資料數量
	 */
	public Integer countMemberPetLike() throws SQLException {
		final String SQL = "SELECT COUNT(*) FROM [paw_poster].[dbo].[member_pet_like]";

		PreparedStatement ps = conn.prepareStatement(SQL);
		ResultSet rs = ps.executeQuery();
		rs.next(); // 此查詢必定會有回傳結果

		int count = rs.getInt(1);

		rs.close();
		ps.close();

		return count;
	}

	/* === Create === */

	/**
	 * 新增 memberPetLike。
	 */
	public void insertLike(MemberPetLike memberPetLike) throws SQLException {
		final String SQL = "INSERT INTO [paw_poster].[dbo].[member_pet_like]([create_date], [member_id], [pet_id]) VALUES(?, ?, ?)";
		PreparedStatement ps = conn.prepareStatement(SQL);
		ps.setDate(1, DateTool.convertUtilToSqlDate(memberPetLike.getCreateDate()));
		ps.setInt(2, memberPetLike.getMember().getMemberId());
		ps.setInt(3, memberPetLike.getPet().getPetId());

		ps.execute();
	}

	/**
	 * 新增 memberPetLikes。
	 */
	public void insertLikes(List<MemberPetLike> memberPetLikes) throws SQLException {
		final String SQL = "INSERT INTO [paw_poster].[dbo].[member_pet_like]([create_date], [member_id], [pet_id]) VALUES(?, ?, ?)";
		PreparedStatement ps = conn.prepareStatement(SQL);

		for (MemberPetLike memberPetLike : memberPetLikes) {
			ps.setDate(1, DateTool.convertUtilToSqlDate(memberPetLike.getCreateDate()));
			ps.setInt(2, memberPetLike.getMember().getMemberId());
			ps.setInt(3, memberPetLike.getPet().getPetId());
			ps.addBatch();
		}

		ps.executeBatch();
		ps.execute();
	}

	/* === Delete === */

	/**
	 * 移除按讚記錄。
	 * 
	 * @param memberPetLike 按讚的物件。
	 */
	public void removeLike(MemberPetLike memberPetLike) throws SQLException {
		final String SQL = "DELETE FROM [paw_poster].[dbo].[member_pet_like] WHERE [member_id] = ? AND [pet_id] = ?";
		PreparedStatement ps = conn.prepareStatement(SQL);
		ps.setInt(1, memberPetLike.getMember().getMemberId());
		ps.setInt(2, memberPetLike.getPet().getPetId());

		ps.execute();
	}
}