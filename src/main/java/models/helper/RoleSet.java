package models.helper;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RoleSet {
    private boolean superAdminRole;
    private boolean userRole;

    public RoleSet(boolean superAdminRole, boolean userRole) {
        this.superAdminRole = superAdminRole;
        this.userRole = userRole;
    }

    public boolean isSuperAdminRole() {
        return superAdminRole;
    }

    public void setSuperAdminRole(boolean superAdminRole) {
        this.superAdminRole = superAdminRole;
    }

    public boolean isUserRole() {
        return userRole;
    }

    public void setUserRole(boolean userRole) {
        this.userRole = userRole;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RoleSet other = (RoleSet) obj;
        if (superAdminRole != other.superAdminRole)
            return false;
        if (userRole != other.userRole)
            return false;

        return true;
    }

    public List<String> getRoleStringList() {
        List<String> roles = new LinkedList<>();
        if (isUserRole()) roles.add(RolesHelper.roleStrUser);
        if (isSuperAdminRole()) roles.add(RolesHelper.roleStrSuperAdmin);
        return roles;
    }

    public static JsonSerializer<RoleSet> getJsonSerializer() {
        return new JsonSerializer<>() {
            @Override
            public JsonElement serialize(RoleSet src, Type typeOfSrc, JsonSerializationContext context) {
                return context.serialize(src.getRoleStringList(), new TypeToken<ArrayList<String>>() {
                }.getType());
            }
        };
    }

    public static JsonDeserializer<RoleSet> getJsonDeserializer() {
        return new JsonDeserializer<>() {
            @Override
            public RoleSet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonArray jsonArray = json.getAsJsonArray();

                return new RoleSet(
                        jsonArray.contains(new JsonPrimitive(RolesHelper.roleStrSuperAdmin)),
                        jsonArray.contains(new JsonPrimitive(RolesHelper.roleStrUser))
                );
            }
        };
    }
}