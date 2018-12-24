package saufverwaltung.util;

import java.util.List;

public interface DbConnection {

    /**
     * Does a certain member exist?
     * 
     * @param name The name of the member.
     * @return true if and only if a member with this name exists.
     */
    boolean doesMemberExist(String name);

    /**
     * Creates a new member object in the database.
     * 
     * @param name The name of the member.
     * @param startCredits The credit of the member.
     */
    void createMember(String name, int startCredits);

    /**
     * Toggles the visible attribute of the member.
     * 
     * @param name The name of the member.
     */
    void toggleVisible(String name);

    /**
     * Adds a certain amount of value to the member's credits.
     * 
     * @param name The name of the member.
     * @param cash The amount of value to add.
     */
    void deposit(String name, int cash);

    /**
     * Deletes a member.
     * 
     * @param name The name of the member to delete.
     */
    void deleteMember(String name);

    /**
     * Adds consumed goods to the member and automatically withdraws from his credits.
     * 
     * @param name The name of the member.
     * @param alk The consumed alk.
     * @param antalk The consumed antalk.
     */
    void updateStats(String name, int alk, int antalk);


    /**
     * Sets the consumed goods of the member without withdrawing from his credits.
     * 
     * @param name The name of the member.
     * @param alk The consumed alk.
     * @param antalk The consumed antalk.
     */
    void setStats(String name, int alk, int antalk);


    /**
     * Returns a string representation of the database's contents, mainly for debugging purposes.
     */
    String getMembersString();

    /**
     * Does a members table exist?
     */
    boolean doesTableExist();

    /**
     * creates an empty table in the database.
     */
    void createEmptyTable();


    /**
     * Returns the database's contents as a list of members.
     * 
     * @return A list containing all members in the database.
     */
    List<Member> getMembers();

    /**
     * Renames a member.
     * 
     * @param oldName The member to rename.
     * @param newName The new name.
     */
    void renameMember(String oldName, String newName);
}
