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
package org.sonar.db.component;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import java.util.Date;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.sonar.api.component.Component;
import org.sonar.api.resources.Scopes;

import static com.google.common.base.Preconditions.checkArgument;
import static org.sonar.db.component.ComponentValidator.checkComponentKey;
import static org.sonar.db.component.ComponentValidator.checkComponentName;

public class ComponentDto implements Component {

  public static final String UUID_PATH_SEPARATOR = ".";
  public static final String UUID_PATH_OF_ROOT = UUID_PATH_SEPARATOR;
  private static final Splitter UUID_PATH_SPLITTER = Splitter.on(UUID_PATH_SEPARATOR).omitEmptyStrings();

  /**
   * ID generated by database. Do not use.
   */
  private Long id;

  /**
   * Non-empty and unique functional key
   */
  private String kee;

  /**
   * Not empty . Max size is 50 (note that effective UUID values are 40 characters with
   * the current algorithm in use). Obviously it is unique.
   * It is generated by Compute Engine.
   */
  private String uuid;

  /**
   * Not empty path of ancestor UUIDS, excluding itself. Value is suffixed by a dot in
   * order to support LIKE conditions when requesting descendants of a component
   * and to avoid Oracle NULL on root components.
   * Example:
   * - on root module: UUID="1" UUID_PATH="."
   * - on module: UUID="2" UUID_PATH=".1."
   * - on directory: UUID="3" UUID_PATH=".1.2."
   * - on file: UUID="4" UUID_PATH=".1.2.3."
   * - on view: UUID="5" UUID_PATH="."
   * - on sub-view: UUID="6" UUID_PATH=".5."
   *
   * @since 6.0
   */
  private String uuidPath;

  /**
   * Non-null UUID of root component. Equals UUID column on root components
   * Example:
   * - on root module: UUID="1" PROJECT_UUID="1"
   * - on module: UUID="2" PROJECT_UUID="1"
   * - on directory: UUID="3" PROJECT_UUID="1"
   * - on file: UUID="4" PROJECT_UUID="1"
   * - on view: UUID="5" PROJECT_UUID="5"
   * - on sub-view: UUID="6" PROJECT_UUID="5"
  */
  private String projectUuid;

  /**
   * Badly named, it is not the root !
   * - on root module: UUID="1" ROOT_UUID="1"
   * - on modules, whatever depth, value is the root module: UUID="2" ROOT_UUID="1"
   * - on directory, value is the closest module: UUID="3" ROOT_UUID="2"
   * - on file, value is the closest module: UUID="4" ROOT_UUID="2"
   * - on view: UUID="5" ROOT_UUID="5"
   * - on sub-view: UUID="6" ROOT_UUID="5"
   * @since 6.0
   */
  private String rootUuid;

  private String moduleUuid;
  private String moduleUuidPath;
  private String copyComponentUuid;
  private String developerUuid;
  private String scope;
  private String qualifier;
  private String path;
  private String deprecatedKey;
  private String name;
  private String longName;
  private String language;
  private String description;
  private boolean enabled = true;

  private Date createdAt;
  private Long authorizationUpdatedAt;

  public Long getId() {
    return id;
  }

  public ComponentDto setId(Long id) {
    this.id = id;
    return this;
  }

  public String uuid() {
    return uuid;
  }

  public ComponentDto setUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public String getUuidPath() {
    return uuidPath;
  }

  /**
   * List of ancestor UUIDs, ordered by depth in tree.
   */
  List<String> getUuidPathAsList() {
    return UUID_PATH_SPLITTER.splitToList(uuidPath);
  }

  public ComponentDto setUuidPath(String s) {
    this.uuidPath = s;
    return this;
  }

  @Override
  public String key() {
    return kee;
  }

  public String scope() {
    return scope;
  }

  public ComponentDto setScope(String scope) {
    this.scope = scope;
    return this;
  }

  @Override
  public String qualifier() {
    return qualifier;
  }

  public ComponentDto setQualifier(String qualifier) {
    this.qualifier = qualifier;
    return this;
  }

  @CheckForNull
  public String deprecatedKey() {
    return deprecatedKey;
  }

  public ComponentDto setDeprecatedKey(@Nullable String deprecatedKey) {
    this.deprecatedKey = deprecatedKey;
    return this;
  }

  /**
   * Return the root project uuid. On a root project, return itself
   */
  public String projectUuid() {
    return projectUuid;
  }

  public ComponentDto setProjectUuid(String projectUuid) {
    this.projectUuid = projectUuid;
    return this;
  }

  public boolean isRoot() {
    return UUID_PATH_OF_ROOT.equals(uuidPath);
  }

  /**
   * Return the direct module of a component. Will be null on projects
   */
  @CheckForNull
  public String moduleUuid() {
    return moduleUuid;
  }

  public ComponentDto setModuleUuid(@Nullable String moduleUuid) {
    this.moduleUuid = moduleUuid;
    return this;
  }

  /**
   * Return the path from the project to the last modules
   */
  public String moduleUuidPath() {
    return moduleUuidPath;
  }

  public ComponentDto setModuleUuidPath(String moduleUuidPath) {
    this.moduleUuidPath = moduleUuidPath;
    return this;
  }

  @CheckForNull
  @Override
  public String path() {
    return path;
  }

  public ComponentDto setPath(@Nullable String path) {
    this.path = path;
    return this;
  }

  @Override
  public String name() {
    return name;
  }

  public ComponentDto setName(String name) {
    this.name = checkComponentName(name);
    return this;
  }

  @Override
  public String longName() {
    return longName;
  }

  public ComponentDto setLongName(String longName) {
    this.longName = longName;
    return this;
  }

  @CheckForNull
  public String language() {
    return language;
  }

  public ComponentDto setLanguage(@Nullable String language) {
    this.language = language;
    return this;
  }

  @CheckForNull
  public String description() {
    return description;
  }

  public ComponentDto setDescription(@Nullable String description) {
    this.description = description;
    return this;
  }

  public String getRootUuid() {
    return rootUuid;
  }

  public ComponentDto setRootUuid(String rootUuid) {
    this.rootUuid = rootUuid;
    return this;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public ComponentDto setEnabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  @CheckForNull
  public String getCopyResourceUuid() {
    return copyComponentUuid;
  }

  public ComponentDto setCopyComponentUuid(@Nullable String copyComponentUuid) {
    this.copyComponentUuid = copyComponentUuid;
    return this;
  }

  @CheckForNull
  public String getDeveloperUuid() {
    return developerUuid;
  }

  public ComponentDto setDeveloperUuid(@Nullable String developerUuid) {
    this.developerUuid = developerUuid;
    return this;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public ComponentDto setCreatedAt(Date datetime) {
    this.createdAt = datetime;
    return this;
  }

  /**
   * Only available on projects
   */
  @CheckForNull
  public Long getAuthorizationUpdatedAt() {
    return authorizationUpdatedAt;
  }

  public ComponentDto setAuthorizationUpdatedAt(@Nullable Long authorizationUpdatedAt) {
    this.authorizationUpdatedAt = authorizationUpdatedAt;
    return this;
  }

  public String getKey() {
    return key();
  }

  public ComponentDto setKey(String key) {
    this.kee = checkComponentKey(key);
    return this;
  }

  public boolean isRootProject() {
    return moduleUuid == null && Scopes.PROJECT.equals(scope);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ComponentDto that = (ComponentDto) o;
    return uuid != null ? uuid.equals(that.uuid) : (that.uuid == null);

  }

  @Override
  public int hashCode() {
    return uuid != null ? uuid.hashCode() : 0;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
      .append("id", id)
      .append("uuid", uuid)
      .append("uuidPath", uuidPath)
      .append("kee", kee)
      .append("scope", scope)
      .append("qualifier", qualifier)
      .append("projectUuid", projectUuid)
      .append("moduleUuid", moduleUuid)
      .append("moduleUuidPath", moduleUuidPath)
      .append("rootUuid", rootUuid)
      .append("copyComponentUuid", copyComponentUuid)
      .append("developerUuid", developerUuid)
      .append("path", path)
      .append("deprecatedKey", deprecatedKey)
      .append("name", name)
      .append("longName", longName)
      .append("language", language)
      .append("enabled", enabled)
      .append("authorizationUpdatedAt", authorizationUpdatedAt)
      .toString();
  }

  public static String formatUuidPathFromParent(ComponentDto parent) {
    checkArgument(!Strings.isNullOrEmpty(parent.getUuidPath()));
    checkArgument(!Strings.isNullOrEmpty(parent.uuid()));
    return parent.getUuidPath() + parent.uuid() + UUID_PATH_SEPARATOR;
  }
}
