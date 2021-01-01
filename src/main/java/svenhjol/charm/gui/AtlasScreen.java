package svenhjol.charm.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmResources;
import svenhjol.charm.base.gui.CharmImageButton;
import svenhjol.charm.container.AtlasContainer;
import svenhjol.charm.container.AtlasInventory;
import svenhjol.charm.container.AtlasInventory.Index;
import svenhjol.charm.message.ServerAtlasTransfer;
import svenhjol.charm.message.ServerAtlasTransfer.MoveMode;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Lukas
 * @since 28.12.2020
 */
public class AtlasScreen extends ContainerScreen<AtlasContainer> {
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Charm.MOD_ID, "textures/gui/atlas_container.png");
    private static final RenderType MAP_BACKGROUND = RenderType.getText(new ResourceLocation("textures/map/map_background.png"));
    private final int slot;
    private MapGui mapGui;
    private int lastSize;

    public AtlasScreen(AtlasContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.passEvents = true;
        this.xSize = 175;
        this.ySize = 171;
        this.slot = inv.getSlotFor(screenContainer.getAtlasInventory().getAtlasItem());
        Map<Index, AtlasInventory.MapInfo> mapInfos = screenContainer.getAtlasInventory().getMapInfos();
        lastSize = mapInfos.size();
        mapGui = mapInfos.size() > 1 ? getWorldMap() : getSingleMap(mapInfos.isEmpty() ? null : mapInfos.values().iterator().next());
    }

    private static boolean isShiftClick() {
        return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340) ||
                InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344);
    }

    private WorldMap getWorldMap() {
        return new WorldMap(80, 16, 48);
    }

    private SingleMap getSingleMap(AtlasInventory.MapInfo mapInfo) {
        return new SingleMap(80, 16, 48, mapInfo);
    }

    @Override
    public void render(@Nonnull MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, partialTicks);
        Map<Index, AtlasInventory.MapInfo> mapInfos = container.getAtlasInventory().getMapInfos();
        int size = mapInfos.size();
        if (mapGui instanceof WorldMap) {
            if (mapInfos.size() <= 1) {
                changeGui(getSingleMap(mapInfos.isEmpty() ? null : mapInfos.values().iterator().next()));
            }
        } else if (mapGui instanceof SingleMap) {
            if (size > lastSize) {
                mapInfos.values().stream().skip(size - 1).findAny().ifPresent(it -> changeGui(getSingleMap(it)));
            }
        }
        lastSize = size;
        mapGui.render(matrices, mouseX, mouseY, partialTicks);
        renderHoveredTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrices, int mouseX, int mouseY) {
        this.font.func_238422_b_(matrices, this.title.func_241878_f(), 8.0F, 6.0F, 4210752);
        this.font.func_238422_b_(matrices, this.playerInventory.getDisplayName().func_241878_f(), 8.0F, (float) ySize - 97, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (minecraft != null) {
            minecraft.getTextureManager().bindTexture(CONTAINER_BACKGROUND);

            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            blit(matrices, x, y, 0, 0, xSize, ySize);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mapGui.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        if (type == ClickType.QUICK_MOVE) {
            Charm.PACKET_HANDLER.sendToServer(new ServerAtlasTransfer(slot, slotIn.getSlotIndex(), -1, MoveMode.FROM_INVENTORY));
        } else {
            super.handleMouseClick(slotIn, slotId, mouseButton, type);
        }
    }

    private void changeGui(MapGui gui) {
        mapGui.close();
        mapGui = gui;
    }

    private interface MapGui {
        void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks);

        default boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }

        default void close() {
        }
    }

    private class WorldMap implements MapGui {
        private final int left;
        private final int top;
        private final int size;

        private WorldMap(int left, int top, int size) {
            this.left = left;
            this.top = top;
            this.size = size;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
            int x = this.left + guiLeft;
            int y = this.top + guiTop;
            final Minecraft mc = Minecraft.getInstance();
            final World world = mc.world;
            if (world == null) {
                return;
            }
            int light = 240;
            float baseScale = (float) size / 128;
            Map<Index, AtlasInventory.MapInfo> mapInfos = container.getAtlasInventory().getMapInfos();
            if (mapInfos.isEmpty()) {
                return;
            }
            int minX = mapInfos.keySet().stream().map(it -> it.x).min(Integer::compareTo).orElseThrow(IllegalStateException::new);
            int maxX = mapInfos.keySet().stream().map(it -> it.x).max(Integer::compareTo).orElseThrow(IllegalStateException::new);
            int minY = mapInfos.keySet().stream().map(it -> it.y).min(Integer::compareTo).orElseThrow(IllegalStateException::new);
            int maxY = mapInfos.keySet().stream().map(it -> it.y).max(Integer::compareTo).orElseThrow(IllegalStateException::new);
            int maxMapDistance = Math.max(maxX + 1 - minX, maxY + 1 - minY);
            float mapSize = (float) size / maxMapDistance;
            matrices.push();
            matrices.translate(x, y, 0);
            matrices.scale(baseScale, baseScale, 1);
            IRenderTypeBuffer.Impl bufferSource = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
            final IVertexBuilder background = bufferSource.getBuffer(MAP_BACKGROUND);
            Matrix4f matrix4f = matrices.getLast().getMatrix();
            background.pos(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).tex(0.0F, 1.0F).lightmap(light).endVertex();
            background.pos(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).tex(1.0F, 1.0F).lightmap(light).endVertex();
            background.pos(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).tex(1.0F, 0.0F).lightmap(light).endVertex();
            background.pos(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).tex(0.0F, 0.0F).lightmap(light).endVertex();
            matrices.pop();
            float mapScale = baseScale / maxMapDistance;
            for (Map.Entry<Index, AtlasInventory.MapInfo> mapInfo : mapInfos.entrySet()) {
                MapData mapData = world.getMapData(FilledMapItem.getMapName(mapInfo.getValue().id));
                if (mapData != null) {
                    matrices.push();
                    matrices.translate(x + mapSize * (mapInfo.getKey().x - minX), y + mapSize * (mapInfo.getKey().y - minY), 1.0);
                    matrices.scale(mapScale, mapScale, 1);
                    mc.gameRenderer.getMapItemRenderer().renderMap(matrices, bufferSource, mapData, false, light);
                    matrices.pop();
                }
            }
            bufferSource.finish();
            drawLines(matrices, x, y, maxMapDistance);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            double normX = normalizeForMapArea(left + guiLeft, mouseX);
            double normY = normalizeForMapArea(top + guiTop, mouseY);
            if (button == 0 && 0 <= normX && normX < 1 && 0 <= normY && normY < 1) {
                ItemStack heldItem = playerInventory.getItemStack();
                AtlasInventory atlasInventory = container.getAtlasInventory();
                if (!heldItem.isEmpty()) {
                    Charm.PACKET_HANDLER.sendToServer(new ServerAtlasTransfer(slot, -1, -1, MoveMode.FROM_HAND));
                } else {
                    Map<Index, AtlasInventory.MapInfo> mapInfos = atlasInventory.getMapInfos();
                    if (!mapInfos.isEmpty()) {
                        int minX = mapInfos.keySet().stream().map(it -> it.x).min(Integer::compareTo).orElseThrow(IllegalStateException::new);
                        int maxX = mapInfos.keySet().stream().map(it -> it.x).max(Integer::compareTo).orElseThrow(IllegalStateException::new);
                        int minY = mapInfos.keySet().stream().map(it -> it.y).min(Integer::compareTo).orElseThrow(IllegalStateException::new);
                        int maxY = mapInfos.keySet().stream().map(it -> it.y).max(Integer::compareTo).orElseThrow(IllegalStateException::new);
                        int maxMapDistance = Math.max(maxX + 1 - minX, maxY + 1 - minY);
                        int x = (int) (normX * maxMapDistance) + minX;
                        int y = (int) (normY * maxMapDistance) + minY;
                        AtlasInventory.MapInfo mapInfo = mapInfos.get(Index.of(x, y));
                        if (mapInfo != null) {
                            if (isShiftClick()) {
                                Charm.PACKET_HANDLER.sendToServer(
                                        new ServerAtlasTransfer(slot, mapInfo.x, mapInfo.z, MoveMode.TO_INVENTORY));
                            } else {
                                changeGui(getSingleMap(mapInfo));
                            }
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private double normalizeForMapArea(double base, double val) {
            return (val - base) / size;
        }

        private void drawLines(MatrixStack matrices, int x, int y, int maxMapDistance) {
            matrices.push();
            matrices.translate(x, y, 2);
            //scale to half size for finer lines
            matrices.scale(0.5f, 0.5f, 1);
            for (int xLine = 1; xLine < maxMapDistance; ++xLine) {
                vLine(matrices, 2 * xLine * size / maxMapDistance, 0, size * 2, -1);
            }
            for (int yLine = 1; yLine < maxMapDistance; ++yLine) {
                hLine(matrices, 0, size * 2, 2 * yLine * size / maxMapDistance, -1);
            }
            matrices.pop();
        }
    }

    private class SingleMap implements MapGui {
        private final int left;
        private final int top;
        private final int size;
        private final AtlasInventory.MapInfo mapInfo;
        private final List<CharmImageButton> buttons = new ArrayList<>();

        public SingleMap(int left, int top, int size, AtlasInventory.MapInfo mapInfo) {
            this.left = left;
            this.top = top;
            this.size = size;
            this.mapInfo = mapInfo;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
            int x = this.left + guiLeft;
            int y = this.top + guiTop;
            final Minecraft mc = Minecraft.getInstance();
            final World world = mc.world;
            if (world == null) {
                return;
            }
            int light = 240;
            float baseScale = (float) size / 128;
            matrices.push();
            matrices.translate(x, y, 0);
            matrices.scale(baseScale, baseScale, 1);
            IRenderTypeBuffer.Impl bufferSource = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
            final IVertexBuilder background = bufferSource.getBuffer(MAP_BACKGROUND);
            Matrix4f matrix4f = matrices.getLast().getMatrix();
            background.pos(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).tex(0.0F, 1.0F).lightmap(light).endVertex();
            background.pos(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).tex(1.0F, 1.0F).lightmap(light).endVertex();
            background.pos(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).tex(1.0F, 0.0F).lightmap(light).endVertex();
            background.pos(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).tex(0.0F, 0.0F).lightmap(light).endVertex();
            if (mapInfo != null) {
                matrices.push();
                matrices.translate(0, 0, 1);
                Minecraft.getInstance().gameRenderer.getMapItemRenderer().renderMap(matrices, bufferSource, world.getMapData(FilledMapItem.getMapName(mapInfo.id)), false, light);
                matrices.pop();
            }
            bufferSource.finish();
            matrices.pop();
            drawButtons(matrices, mouseX, mouseY, partialTicks);
        }

        private void drawButtons(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
            if (buttons.isEmpty()) setupButtons();
            AtlasInventory inventory = container.getAtlasInventory();
            Map<Index, AtlasInventory.MapInfo> mapInfos = inventory.getMapInfos();
            if (mapInfo != null) {
                int mapX = inventory.convertCoordToIndex(mapInfo.x);
                int mapY = inventory.convertCoordToIndex(mapInfo.z);
                buttons.get(0).active = mapInfos.containsKey(Index.of(mapX - 1, mapY));
                buttons.get(1).active = mapInfos.containsKey(Index.of(mapX, mapY - 1));
                buttons.get(2).active = mapInfos.containsKey(Index.of(mapX + 1, mapY));
                buttons.get(3).active = mapInfos.containsKey(Index.of(mapX, mapY + 1));
            } else {
                buttons.forEach(it -> it.active = false);
            }
            buttons.get(4).active = mapInfo == null && !mapInfos.isEmpty() || mapInfos.size() > 1;
            buttons.forEach(it -> it.render(matrices, mouseX, mouseY, partialTicks));
        }

        private void setupButtons() {
            int x = this.left + guiLeft;
            int y = this.top + guiTop;
            int buttonSize = 10;
            int center = (size - buttonSize) / 2;
            //distance of buttons from map
            int dist = 3;
            buttons.add(new CharmImageButton(x - buttonSize - dist, y + center, buttonSize, buttonSize, 80, 0,
                    10, 20, CharmResources.INVENTORY_BUTTONS, click -> buttonClick(-1, 0)));
            buttons.add(new CharmImageButton(x + center, y - buttonSize - dist, buttonSize, buttonSize, 50, 0,
                    10, 20, CharmResources.INVENTORY_BUTTONS, click -> buttonClick(0, -1)));
            buttons.add(new CharmImageButton(x + size + dist, y + center, buttonSize, buttonSize, 70, 0,
                    10, 20, CharmResources.INVENTORY_BUTTONS, click -> buttonClick(1, 0)));
            buttons.add(new CharmImageButton(x + center, y + size + dist, buttonSize, buttonSize, 60, 0,
                    10, 20, CharmResources.INVENTORY_BUTTONS, click -> buttonClick(0, 1)));
            buttons.add(new CharmImageButton(guiLeft + xSize - 19, guiTop + 3, 16, 16, 90, 0,
                    17, 34, CharmResources.INVENTORY_BUTTONS, click -> changeGui(getWorldMap())));
            children.addAll(buttons);
        }

        private void buttonClick(int xDiff, int yDiff) {
            AtlasInventory inventory = container.getAtlasInventory();
            int mapX = inventory.convertCoordToIndex(mapInfo.x);
            int mapY = inventory.convertCoordToIndex(mapInfo.z);
            AtlasInventory.MapInfo mapInfo = inventory.getMapInfos().get(Index.of(mapX + xDiff, mapY + yDiff));
            if (mapInfo != null) {
                changeGui(getSingleMap(mapInfo));
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && guiLeft + left <= mouseX && mouseX < guiLeft + left + size && guiTop + top <= mouseY && mouseY < guiTop + top + size) {
                ItemStack heldItem = playerInventory.getItemStack();
                if (!heldItem.isEmpty()) {
                    Charm.PACKET_HANDLER.sendToServer(new ServerAtlasTransfer(slot, -1, -1, MoveMode.FROM_HAND));
                } else if (mapInfo != null) {
                    if (isShiftClick()) {
                        Charm.PACKET_HANDLER.sendToServer(new ServerAtlasTransfer(slot, mapInfo.x, mapInfo.z, MoveMode.TO_INVENTORY));
                    } else {
                        Charm.PACKET_HANDLER.sendToServer(new ServerAtlasTransfer(slot, mapInfo.x, mapInfo.z, MoveMode.TO_HAND));
                    }
                    changeGui(getSingleMap(null));
                }
                return true;
            }
            return false;
        }

        @Override
        public void close() {
            children.removeAll(buttons);
        }
    }
}
