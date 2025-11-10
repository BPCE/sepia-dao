package fr.groupebpce.sepia.dao.showcase;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("QueryDao Showcase Test Suite")
@SelectPackages({"fr.groupebpce.sepia.dao.showcase.populate", "fr.groupebpce.sepia.dao.showcase.test"})
@IncludeClassNamePatterns({".*Data", ".*Test"})
public class ShowcaseSuite {
}
