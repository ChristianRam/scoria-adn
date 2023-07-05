package com.ceiba.factura.adaptador.dao;

import com.ceiba.factura.modelo.dto.ResumenFacturaDTO;
import com.ceiba.factura.modelo.entidad.EstadoFactura;
import com.ceiba.infraestructura.jdbc.MapperResult;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MapeoFacturaResumen implements RowMapper<ResumenFacturaDTO>, MapperResult {

    @Override
    public ResumenFacturaDTO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        long id = resultSet.getLong("id");
        BigDecimal valorTotal = resultSet.getBigDecimal("valor_total");
        EstadoFactura estado = EstadoFactura.valueOf(resultSet.getString("estado"));

        return new ResumenFacturaDTO(id, valorTotal, estado);
    }

}
