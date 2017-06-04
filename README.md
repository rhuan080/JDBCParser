# JDBCParser

JDBCParser is the API to convert the ResultSet of JDBC to Java Beans. The goal is to create Beans as ResultSet, without requiring Annotations or Mapped Beans. With JDBCParser you can:

   Parse the ResulSet to JavaBeans with or without relations with other Beans, and all without Annotations or Map. If the Bean converted contains a list of other beans and the ResultSet contains data compatible with this list, then list is build by JDBCParser.
