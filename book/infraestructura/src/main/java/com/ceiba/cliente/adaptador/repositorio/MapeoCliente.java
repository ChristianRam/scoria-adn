package com.ceiba.cliente.adaptador.repositorio;

import com.ceiba.cliente.entidad.Cliente;
import com.ceiba.cliente.entidad.TipoCliente;
import com.ceiba.infraestructura.jdbc.MapperResult;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapeoCliente implements RowMapper<Cliente>, MapperResult {

    @Override
    public Cliente mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        long id = resultSet.getLong("id");
        String nombre = resultSet.getString("nombre");
        TipoCliente tipoCliente = TipoCliente.valueOf(resultSet.getString("tipo_cliente"));

        return Cliente.reconstruir(id, nombre, tipoCliente);
    }

}
