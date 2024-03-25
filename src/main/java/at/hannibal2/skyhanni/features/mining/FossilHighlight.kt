package at.hannibal2.skyhanni.features.inventory

import at.hannibal2.skyhanni.SkyHanniMod
import at.hannibal2.skyhanni.config.ConfigUpdaterMigrator
import at.hannibal2.skyhanni.events.GuiContainerEvent
import at.hannibal2.skyhanni.utils.InventoryUtils.getInventoryName
import at.hannibal2.skyhanni.utils.InventoryUtils.getUpperItems
import at.hannibal2.skyhanni.utils.ItemUtils.getInternalNameOrNull
import at.hannibal2.skyhanni.utils.ItemUtils.getLore
import at.hannibal2.skyhanni.utils.ItemUtils.itemName
import at.hannibal2.skyhanni.utils.LorenzColor
import at.hannibal2.skyhanni.utils.LorenzUtils
import at.hannibal2.skyhanni.utils.NEUItems.getPriceOrNull
import at.hannibal2.skyhanni.utils.NumberUtil.formatLong
import at.hannibal2.skyhanni.utils.RenderUtils.highlight
import at.hannibal2.skyhanni.utils.StringUtils.matchMatcher
import at.hannibal2.skyhanni.utils.repopatterns.RepoPattern
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object FossilHighlight {

    private val config get() = SkyHanniMod.feature.mining.fossilHighlighter

    var found = false
    var drawn = false

    @SubscribeEvent
    fun onWindowclose(event: GuiContainerEvent.CloseWindowEvent) {
        found = false
        drawn = false
    }

    @SubscribeEvent
    fun onBackgroundDrawn(event: GuiContainerEvent.BackgroundDrawnEvent) {
        if (!LorenzUtils.inSkyBlock) return
        if(!config) return
        if (event.gui !is GuiChest) return

        val guiChest = event.gui
        val chest = guiChest.inventorySlots as ContainerChest
        if (!chest.getInventoryName().contains("Fossil")) return
        var slots = chest.getUpperItems()
        var fossiltype: String? = null

        for ((slot, stack) in slots) {
            var tag: net.minecraft.nbt.NBTTagCompound? = stack.tagCompound
            if(tag != null) {
                var lore = stack.getLore()
                for(i in lore) {
                    if(i.contains("Progress")) {
                        if(!found) {
                            var percentage = ""
                            var aftercolon = false
                            for (char in i) {
                                if (aftercolon) {
                                    percentage += char
                                }
                                if (char == ':') {
                                    aftercolon = true
                                }
                            }
                            if(percentage.contains("10%")){fossiltype = "Webbed"}
                            if(percentage.contains("8.3%")){fossiltype = "Spine"}//at.hannibal2.skyhanni.utils.ChatUtils.chat("Spine")//
                            if(percentage.contains("7.1%")){fossiltype = "Helix"}//at.hannibal2.skyhanni.utils.ChatUtils.chat("Helix")//
                            if(percentage.contains("7.7%")){fossiltype = "Footprint or Claw"}//at.hannibal2.skyhanni.utils.ChatUtils.chat("Footprint or Claw")//
                            if(percentage.contains("12.5%")){fossiltype = "Tusk"}//at.hannibal2.skyhanni.utils.ChatUtils.chat("Tusk")//
                            if(percentage.contains("9.1%")){fossiltype = "Clubbed"}//at.hannibal2.skyhanni.utils.ChatUtils.chat("Clubbed")//
                            if(percentage.contains("6.2%")){fossiltype = "Ugly"}//at.hannibal2.skyhanni.utils.ChatUtils.chat("Ugly")//
                        }
                        found = true
                    }
                    if(stack.displayName.contains("§6Fossil")) {
                        slot highlight LorenzColor.RED
                        if(fossiltype!= null){at.hannibal2.skyhanni.utils.ChatUtils.chat(fossiltype)}
                    }
                }
            }
        }
        if(fossiltype != null){
            drawgui(fossiltype)
        }
    }

    fun drawgui(type: String){
        //i tried adding a text showing which Fossil you found next to the inventory but i couldnt manage to do it
        at.hannibal2.skyhanni.utils.GuiRenderUtils.drawString("Found fossil: $type", 175, 175)
    }
}

