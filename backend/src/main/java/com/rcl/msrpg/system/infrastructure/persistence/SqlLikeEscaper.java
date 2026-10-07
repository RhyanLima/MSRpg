package com.rcl.msrpg.system.infrastructure.persistence;

final class SqlLikeEscaper {

    private SqlLikeEscaper() {}

    static String escape(String term) {
        return term
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_");
    }

}
