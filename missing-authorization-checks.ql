import java
import semmle.code.java.controlflow.Guards

from MethodAccess methodAccess, Guard guard
where
  methodAccess.getMethod().hasName("getSensitiveData") and
  not exists(guard | guard.isAuthorization())
select methodAccess, "This method access lacks authorization check."
