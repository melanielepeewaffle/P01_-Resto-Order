package ch.hearc.ig.orderresto.persistence.mapper;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.persistence.util.DataBaseConnection;
import ch.hearc.ig.orderresto.persistence.util.DataBaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrivateCustomerMapper extends AbstractCustomerMapper {

    @Override
    public void insert(Customer customer) throws SQLException {
        PrivateCustomer privateCustomer = (PrivateCustomer) customer;
        String sql = "INSERT INTO CLIENT (EMAIL, TELEPHONE, NOM, CODE_POSTAL, LOCALITE, RUE, NUM_RUE, PAYS, TYPE, EST_UNE_FEMME, PRENOM) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'P', ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepareStatementForCommonFields(ps, privateCustomer, 1);
            ps.setString(9, privateCustomer.getGender().equalsIgnoreCase("F") ? "O" : "N");
            ps.setString(10, privateCustomer.getFirstName());
            ps.executeUpdate();

            long generatedId = DataBaseUtils.getGeneratedKey(conn, "SEQ_CLIENT");
            privateCustomer.setId(generatedId);
            customerIdentityMap.put(generatedId, privateCustomer);
        }
    }

    @Override
    public void update(Customer customer) throws SQLException {
        PrivateCustomer privateCustomer = (PrivateCustomer) customer;
        String sql = "UPDATE CLIENT SET EMAIL = ?, TELEPHONE = ?, NOM = ?, CODE_POSTAL = ?, LOCALITE = ?, RUE = ?, NUM_RUE = ?, PAYS = ?, EST_UNE_FEMME = ?, PRENOM = ? " +
                "WHERE NUMERO = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepareStatementForCommonFields(ps, privateCustomer, 1);
            ps.setString(9, privateCustomer.getGender().equalsIgnoreCase("F") ? "O" : "N");
            ps.setString(10, privateCustomer.getFirstName());
            ps.setLong(11, privateCustomer.getId());
            ps.executeUpdate();
            customerIdentityMap.put(privateCustomer.getId(), privateCustomer);
        }
    }

    @Override
    protected Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Address address = addressMapper.mapResultSetToAddress(rs, 4);
        return new PrivateCustomer(
                rs.getLong("NUMERO"),
                rs.getString("TELEPHONE"),
                rs.getString("EMAIL"),
                address,
                rs.getString("EST_UNE_FEMME").equals("O") ? "F" : "M",
                rs.getString("PRENOM"),
                rs.getString("NOM")
        );
    }
}
