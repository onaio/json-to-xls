package io.ei.jsontoxls.repository;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface ExcelRepository {
    @SqlUpdate("INSERT INTO excel (token, template_token, data) VALUES (:token, :template_token, :data)")
    void add(@Bind("token") String token, @Bind("template_token") String templateToken, @Bind("data") byte[] data);

    @SqlQuery("SELECT data FROM excel WHERE token = :token")
    byte[] findByToken(@Bind("token") String token);

    @SqlUpdate("DELETE FROM excel WHERE created_timestamp < current_timestamp - interval '24' hour")
    void deleteAllExcelsCreatedADayBefore();
}
