/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.platform;

import java.util.Properties;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.CoreProperties;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.utils.DateUtils;
import org.sonar.api.utils.System2;
import org.sonar.db.DbTester;
import org.sonar.db.property.PropertyDto;

import static org.assertj.core.api.Assertions.assertThat;

public class StartupMetadataPersisterTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Rule
  public DbTester dbTester = DbTester.create(System2.INSTANCE);

  private PersistentSettings persistentSettings = new PersistentSettings(dbTester.getDbClient(), new ServerSettingsImpl(new PropertyDefinitions(), new Properties()));
  private StartupMetadata metadata = new StartupMetadata("an_id", 123_456_789L);
  private StartupMetadataPersister underTest = new StartupMetadataPersister(metadata, persistentSettings);

  @Test
  public void persist_metadata_at_startup() {
    underTest.start();

    assertPersistedProperty(CoreProperties.SERVER_ID, metadata.getStartupId());
    assertPersistedProperty(CoreProperties.SERVER_STARTTIME, DateUtils.formatDateTime(metadata.getStartedAt()));

    underTest.stop();
  }

  private void assertPersistedProperty(String propertyKey, String expectedValue) {
    PropertyDto prop = dbTester.getDbClient().propertiesDao().selectGlobalProperty(dbTester.getSession(), propertyKey);
    assertThat(prop.getValue()).isEqualTo(expectedValue);
  }
}
