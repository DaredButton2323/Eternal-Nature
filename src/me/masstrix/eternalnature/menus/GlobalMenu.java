/*
 * Copyright 2020 Matthew Denton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.masstrix.eternalnature.menus;

import me.masstrix.eternalnature.core.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class GlobalMenu {

    static final ItemStack BACK_ICON = new ItemBuilder(Material.ARROW)
            .setName("&aGo Back").build();

    private final String ID;
    private Inventory inventory;
    private List<Button> buttons = new ArrayList<>();

    public GlobalMenu(Menus id, String name, int rows) {
        this(id.getId(), name, rows);
    }

    public GlobalMenu(String id, String name, int rows) {
        this.ID = id;
        this.inventory = Bukkit.createInventory(null, rows * 9, name);
    }

    public final String getID() {
        return ID;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public final void setIcon(int slot, ItemStack stack) {
        inventory.setItem(slot, stack);
    }

    public final void setButton(Button button) {
        this.buttons.add(button);
        button.update();
    }

    public final void addBackButton(MenuManager manager, Menus id) {
        addBackButton(manager, id.getId());
    }

    public final void addBackButton(MenuManager manager, String id) {
        setButton(new Button(getInventory(), 0, BACK_ICON).onClick(player -> {
            manager.getMenu(id).open(player);
        }));
    }

    public final void openMenu(MenuManager manager, Menus id, Player player) {
        openMenu(manager, id.getId(), player);
    }

    public final void openMenu(MenuManager manager, String id, Player player) {
        GlobalMenu menu = manager.getMenu(id);
        if (menu != null)
            menu.open(player);
    }

    /**
     * Makes the player open the menu.
     *
     * @param player player who will open the menu.
     */
    public void open(Player player) {
        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
    }

    /**
     * Called when a slot of the inventory is clicked.
     *
     * @param slot slot clicked.
     * @param player player who clicked the slot.
     * @param clickType type of click the player preformed.
     */
    public void onClick(int slot, Player player, ClickType clickType) {}

    /**
     * Processes a click on this inventory. This will also trigger the onClick event
     * and call for a click on any buttons that were added to the menu.
     *
     * @param slot slot that was clicked.
     * @param player player who clicked the slot.
     * @param clickType type of click the player preformed.
     */
    final void processClick(int slot, Player player, ClickType clickType) {
        onClick(slot, player, clickType);
        buttons.forEach(b -> b.click(player, this.inventory, slot));
    }

    /**
     * Converts a row and column into an inventory slot.
     *
     * @param row row to get.
     * @param column column to get.
     * @return slot where row and column intersect.
     */
    public final int asSlot(int row, int column) {
        return (row * 9) + column;
    }

    /**
     * Returns if a inventory is similar or the same to this inventory. Note there is a
     * very small chance this can cause a false positive.
     *
     * @param inv inventory to compare to.
     * @return if the inventory is similar or the same.
     */
    final boolean isInventorySimilar(Inventory inv) {
        if (inv == inventory) return true;
        if (inv.getType() != inventory.getType()) return false;
        if (inv.getSize() != inventory.getSize()) return false;
        if (!Arrays.equals(inventory.getContents(), inv.getContents())) return false;
        return true;
    }
}
