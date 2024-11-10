package ch.hearc.ig.orderresto.persistence.mapper;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.persistence.util.DataBaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrganizationCustomerMapper extends AbstractCustomerMapper {

    @Override
    public void insert(Connection conn, Customer customer) throws SQLException {
        OrganizationCustomer orgCustomer = (OrganizationCustomer) customer;

        String sql = "INSERT INTO CLIENT (EMAIL, TELEPHONE, PAYS, CODE_POSTAL, LOCALITE, RUE, NUM_RUE, TYPE, NOM, FORME_SOCIALE) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 'O', ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            prepareStatementForCommonFields(ps, orgCustomer, 1);
            ps.setString(8, orgCustomer.getName());
            ps.setString(9, orgCustomer.getLegalForm());

            ps.executeUpdate();

            long generatedId = DataBaseUtils.getGeneratedKey(conn, "SEQ_CLIENT");
            orgCustomer.setId(generatedId);
            customerIdentityMap.put(generatedId, orgCustomer);
        }
    }

    @Override
    public void update(Connection conn, Customer customer) throws SQLException {
        OrganizationCustomer orgCustomer = (OrganizationCustomer) customer;

        String sql = "UPDATE CLIENT SET EMAIL = ?, TELEPHONE = ?, PAYS = ?, CODE_POSTAL = ?, LOCALITE = ?, RUE = ?, NUM_RUE = ?, NOM = ?, FORME_SOCIALE = ? " +
                "WHERE NUMERO = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            prepareStatementForCommonFields(ps, orgCustomer, 1);
            ps.setString(8, orgCustomer.getName());
            ps.setString(9, orgCustomer.getLegalForm());
            ps.setLong(10, orgCustomer.getId());
            ps.executeUpdate();
            customerIdentityMap.put(orgCustomer.getId(), orgCustomer);
        }
    }

    public OrganizationCustomer findByEmail(Connection conn, String email) throws SQLException {
        String sql = "SELECT * FROM CLIENT WHERE EMAIL = ? AND TYPE = 'O'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return (OrganizationCustomer) mapResultSetToCustomer(rs);
                }
            }
        }

        return null;
    }

    @Override
    protected Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Address address = addressMapper.mapResultSetToAddress(rs, 4);
        return new OrganizationCustomer(
                rs.getLong("NUMERO"),
                rs.getString("TELEPHONE"),
                rs.getString("EMAIL"),
                address,
                rs.getString("NOM"),
                rs.getString("FORME_SOCIALE")
        );
    }
}