package svenhjol.charm.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmResources;
import svenhjol.charm.base.gui.AbstractCharmContainerScreen;
import svenhjol.charm.base.gui.CharmImageButton;
import svenhjol.charm.base.helper.MapRenderHelper;
import svenhjol.charm.container.AtlasContainer;
import svenhjol.charm.container.AtlasInventory;
import svenhjol.charm.container.AtlasInventory.Index;
import svenhjol.charm.message.ServerAtlasTransfer;
import svenhjol.charm.message.ServerAtlasTransfer.MoveMode;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Lukas
 * @since 28.12.2020
 */
public class AtlasScreen extends AbstractCharmContainerScreen<AtlasContainer> {
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Charm.MOD_ID, "textures/gui/atlas_container.png");
    private static final RenderType MAP_DECORATIONS = RenderType.getText(new ResourceLocation("textures/map/map_icons.png"));
    private static final RenderType LINES = RenderType.makeType("lines", DefaultVertexFormats.POSITION_COLOR, 7, 256, RenderType.State.getBuilder().build(false));
    private static final int SIZE = 48;
    private static final int LEFT = 74;
    private static final int TOP = 16;
    private static final int BUTTON_SIZE = 9;
    private static final int BUTTON_DISTANCE = 3;
    private static final int CENTER = (SIZE - BUTTON_SIZE) / 2;
    private static final int MAX_MAPS = 8;
    private static final int NORMAL_SIZE = 128;
    private static final float BASE_SCALE = (float) SIZE / NORMAL_SIZE;
    private static final int LIGHT = 240;
    private final int slot;
    private final Map<ButtonDirection, CharmImageButton> buttons;
    private final WorldMap worldMap = new WorldMap();
    private final SingleMap singleMap = new SingleMap(null);
    private MapGui mapGui;
    private int lastSize;

    public AtlasScreen(AtlasContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn, CONTAINER_BACKGROUND);
        this.xSize = 175;
        this.ySize = 168;
        this.slot = inv.getSlotFor(screenContainer.getAtlasInventory().getAtlasItem());
        Map<Index, AtlasInventory.MapInfo> mapInfos = screenContainer.getAtlasInventory().getMapInfos();
        lastSize = mapInfos.size();
        mapGui = mapInfos.size() > 1 ? getWorldMap() : getSingleMap(mapInfos.isEmpty() ? null : mapInfos.values().iterator().next());
        buttons = new EnumMap<>(ButtonDirection.class);
        for (ButtonDirection direction : ButtonDirection.values()) {
            buttons.put(direction, createButton(direction));
        }
    }

    private CharmImageButton createButton(ButtonDirection dir) {
        return new CharmImageButton(() -> guiLeft + LEFT + dir.left, () -> guiTop + TOP + dir.top, dir.width, dir.height,
                dir.texStart, 0, dir.height, 2 * dir.height, CharmResources.INVENTORY_BUTTONS, it -> mapGui.buttonClick(dir));
    }

    private static boolean isShiftClick() {
        long handle = Minecraft.getInstance().getMainWindow().getHandle();
        return InputMappings.isKeyDown(handle, 340) || InputMappings.isKeyDown(handle, 344);
    }

    private WorldMap getWorldMap() {
        worldMap.fixedMapDistance = false;
        return worldMap;
    }

    private SingleMap getSingleMap(AtlasInventory.MapInfo mapInfo) {
        singleMap.mapInfo = mapInfo;
        return singleMap;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        updateGui();
        updateButtonState();
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrices, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrices, mouseX, mouseY);
        MapRenderHelper.renderMapWithBackground(matrices, LEFT, TOP, 0, BASE_SCALE, LIGHT, bufferSource -> mapGui.render(matrices, bufferSource, mouseX, mouseY));
    }

    private void updateGui() {
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
    }

    private void updateButtonState() {
        buttons.forEach((direction, button) -> {
            button.visible = mapGui.buttonVisible(direction);
            if (button.visible) {
                button.active = mapGui.buttonEnabled(direction);
                if (!super.buttons.contains(button)) addButton(button);
            } else {
                removeButton(button);
            }
        });
    }

    private void removeButton(Widget button) {
        super.buttons.remove(button);
        super.children.remove(button);
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
        mapGui = gui;
    }

    private void renderDecorations(MatrixStack matrices, IRenderTypeBuffer buffer, MapData mapData, float relativeScale, Predicate<MapDecoration> filter) {
        int k = 0;

        for (MapDecoration mapdecoration : mapData.mapDecorations.values()) {
            if (!filter.test(mapdecoration) || mapdecoration.render(k)) {
                ++k;
                continue;
            }
            matrices.push();
            matrices.translate(mapdecoration.getX() / 2f + 64, mapdecoration.getY() / 2f + 64, 0.02);
            matrices.rotate(Vector3f.ZP.rotationDegrees(mapdecoration.getRotation() * 22.5f));
            matrices.scale(relativeScale * 4, relativeScale * 4, 3);
            matrices.translate(-0.125, 0.125, 0);
            byte b0 = mapdecoration.getImage();
            float f1 = (float) (b0 % 16) / 16f;
            float f2 = (float) (b0 / 16) / 16f;
            float f3 = (float) (b0 % 16 + 1) / 16f;
            float f4 = (float) (b0 / 16 + 1) / 16f;
            Matrix4f matrix4f = matrices.getLast().getMatrix();
            IVertexBuilder builder = buffer.getBuffer(MAP_DECORATIONS);
            float z = k * 0.001f;
            builder.pos(matrix4f, -1, 1, z).color(255, 255, 255, 255).tex(f1, f2).lightmap(LIGHT).endVertex();
            builder.pos(matrix4f, 1, 1, z).color(255, 255, 255, 255).tex(f3, f2).lightmap(LIGHT).endVertex();
            builder.pos(matrix4f, 1, -1, z).color(255, 255, 255, 255).tex(f3, f4).lightmap(LIGHT).endVertex();
            builder.pos(matrix4f, -1, -1, z).color(255, 255, 255, 255).tex(f1, f4).lightmap(LIGHT).endVertex();
            matrices.pop();

            ++k;
        }
    }

    private enum ButtonDirection {
        LEFT(-BUTTON_SIZE - BUTTON_DISTANCE, CENTER, BUTTON_SIZE, BUTTON_SIZE, 77, -1, 0),
        TOP(CENTER, -BUTTON_SIZE - BUTTON_DISTANCE, BUTTON_SIZE, BUTTON_SIZE, 50, 0, -1),
        RIGHT(SIZE + BUTTON_DISTANCE, CENTER, BUTTON_SIZE, BUTTON_SIZE, 68, 1, 0),
        BOTTOM(CENTER, SIZE + BUTTON_DISTANCE, BUTTON_SIZE, BUTTON_SIZE, 59, 0, 1),
        BACK(82, -12, 16, 16, 86, 0, 0),
        OUT(78, SIZE + BUTTON_DISTANCE, 8, 9, 102, 0, 0),
        IN(86, SIZE + BUTTON_DISTANCE, 8, 9, 110, 0, 0);
        final int left;
        final int top;
        final int width;
        final int height;
        final int texStart;
        final int x;
        final int y;

        ButtonDirection(int left, int top, int width, int height, int texStart, int x, int y) {
            this.left = left;
            this.top = top;
            this.width = width;
            this.height = height;
            this.texStart = texStart;
            this.x = x;
            this.y = y;
        }
    }

    private interface MapGui {
        void render(MatrixStack matrices, IRenderTypeBuffer.Impl bufferSource, int mouseX, int mouseY);

        default boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }

        void buttonClick(ButtonDirection direction);

        boolean buttonVisible(ButtonDirection direction);

        boolean buttonEnabled(ButtonDirection direction);
    }

    private class WorldMap implements MapGui {
        private Index corner = null;
        private int minX = 0;
        private int maxX = 0;
        private int minY = 0;
        private int maxY = 0;
        private int maxMapDistance = 1;
        private int mapDistance = 1;
        private boolean fixedMapDistance = false;

        private WorldMap() {
        }

        private boolean updateExtremes() {
            AtlasInventory inventory = container.getAtlasInventory();
            Map<Index, AtlasInventory.MapInfo> mapInfos = inventory.getMapInfos();
            if (mapInfos.isEmpty()) {
                return false;
            }
            minX = mapInfos.keySet().stream().map(it -> it.x).min(Integer::compareTo).orElseThrow(IllegalStateException::new);
            maxX = mapInfos.keySet().stream().map(it -> it.x).max(Integer::compareTo).orElseThrow(IllegalStateException::new);
            minY = mapInfos.keySet().stream().map(it -> it.y).min(Integer::compareTo).orElseThrow(IllegalStateException::new);
            maxY = mapInfos.keySet().stream().map(it -> it.y).max(Integer::compareTo).orElseThrow(IllegalStateException::new);
            maxMapDistance = Math.max(maxX + 1 - minX, maxY + 1 - minY);
            if (maxMapDistance > MAX_MAPS) {
                maxMapDistance = MAX_MAPS;
            }
            if (fixedMapDistance) {
                mapDistance = Math.min(mapDistance, maxMapDistance);
            } else {
                mapDistance = maxMapDistance;
            }
            if (mapDistance < maxMapDistance || mapDistance == MAX_MAPS) {
                if (corner == null) {
                    int x1 = inventory.convertCoordToIndex((int) (playerInventory.player.getPosX() + 64)) - MAX_MAPS / 2;
                    x1 = Math.max(minX, Math.min(maxX + 1 - MAX_MAPS, x1));
                    int y1 = inventory.convertCoordToIndex((int) (playerInventory.player.getPosZ() + 64)) - MAX_MAPS / 2;
                    y1 = Math.max(minY, Math.min(maxY + 1 - MAX_MAPS, y1));
                    corner = Index.of(x1, y1);
                }
            } else {
                corner = null;
            }
            return true;
        }

        @Override
        public void render(MatrixStack matrices, IRenderTypeBuffer.Impl bufferSource, int mouseX, int mouseY) {
            final Minecraft mc = Minecraft.getInstance();
            final World world = mc.world;
            if (world == null || !updateExtremes()) {
                return;
            }
            float mapSize = (float) NORMAL_SIZE / mapDistance;
            float mapScale = 1f / mapDistance;
            int currentMinX = corner != null ? corner.x : minX;
            int currentMinY = corner != null ? corner.y : minY;
            for (Map.Entry<Index, AtlasInventory.MapInfo> mapInfo : container.getAtlasInventory().getMapInfos().entrySet()) {
                Index key = mapInfo.getKey();
                if (corner != null && (corner.x > key.x || key.x >= corner.x + mapDistance || corner.y > key.y || key.y >= corner.y + mapDistance)) {
                    continue;
                }
                MapData mapData = world.getMapData(FilledMapItem.getMapName(mapInfo.getValue().id));
                if (mapData != null) {
                    matrices.push();
                    matrices.translate(mapSize * (key.x - currentMinX), mapSize * (key.y - currentMinY), 0.1);
                    matrices.scale(mapScale, mapScale, 1);
                    mc.gameRenderer.getMapItemRenderer().renderMap(matrices, bufferSource, mapData, false, LIGHT);
                    matrices.translate(0, 0, 0.2);
                    renderDecorations(matrices, bufferSource, mapData, 1.5f * mapDistance,
                            it -> it.getType() != MapDecoration.Type.PLAYER_OFF_MAP && it.getType() != MapDecoration.Type.PLAYER_OFF_LIMITS);
                    matrices.pop();
                }
            }
            drawLines(matrices, bufferSource.getBuffer(LINES));
        }

        private void drawLines(MatrixStack matrices, IVertexBuilder builder) {
            matrices.push();
            matrices.translate(0, 0, 0.2);
            //need to revert the base scale to avoid some lines being to thin to be drawn
            matrices.scale(0.5f / BASE_SCALE, 0.5f / BASE_SCALE, 1);
            for (int xLine = 1; xLine < mapDistance; ++xLine) {
                vLine(matrices, builder, xLine * 2 * SIZE / mapDistance, 0, 2 * SIZE, -1);
            }
            for (int yLine = 1; yLine < mapDistance; ++yLine) {
                hLine(matrices, builder, 0, 2 * SIZE, yLine * 2 * SIZE / mapDistance, -1);
            }
            matrices.pop();
        }

        private void hLine(MatrixStack matrixStack, IVertexBuilder builder, int minX, int maxX, int y, int color) {
            fill(matrixStack, builder, minX, y, maxX + 1, y + 1, color);
        }

        private void vLine(MatrixStack matrixStack, IVertexBuilder builder, int x, int minY, int maxY, int color) {
            fill(matrixStack, builder, x, minY + 1, x + 1, maxY, color);
        }

        private void fill(MatrixStack matrices, IVertexBuilder builder, int minX, int minY, int maxX, int maxY, int color) {
            if (minX < maxX) {
                int i = minX;
                minX = maxX;
                maxX = i;
            }

            if (minY < maxY) {
                int j = minY;
                minY = maxY;
                maxY = j;
            }

            float f3 = (float) (color >> 24 & 255) / 255.0F;
            float f = (float) (color >> 16 & 255) / 255.0F;
            float f1 = (float) (color >> 8 & 255) / 255.0F;
            float f2 = (float) (color & 255) / 255.0F;
            Matrix4f matrix = matrices.getLast().getMatrix();
            builder.pos(matrix, (float) minX, (float) maxY, 0.0F).color(f, f1, f2, f3).endVertex();
            builder.pos(matrix, (float) maxX, (float) maxY, 0.0F).color(f, f1, f2, f3).endVertex();
            builder.pos(matrix, (float) maxX, (float) minY, 0.0F).color(f, f1, f2, f3).endVertex();
            builder.pos(matrix, (float) minX, (float) minY, 0.0F).color(f, f1, f2, f3).endVertex();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            double normX = normalizeForMapArea(LEFT + guiLeft, mouseX);
            double normY = normalizeForMapArea(TOP + guiTop, mouseY);
            if (button == 0 && 0 <= normX && normX < 1 && 0 <= normY && normY < 1) {
                ItemStack heldItem = playerInventory.getItemStack();
                if (!heldItem.isEmpty()) {
                    Charm.PACKET_HANDLER.sendToServer(new ServerAtlasTransfer(slot, -1, -1, MoveMode.FROM_HAND));
                } else {
                    if (updateExtremes()) {
                        int currentMinX = corner != null ? corner.x : minX;
                        int currentMinY = corner != null ? corner.y : minY;
                        int x = (int) (normX * mapDistance) + currentMinX;
                        int y = (int) (normY * mapDistance) + currentMinY;
                        AtlasInventory.MapInfo mapInfo = container.getAtlasInventory().getMapInfos().get(Index.of(x, y));
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

        @Override
        public void buttonClick(ButtonDirection direction) {
            switch (direction) {
                case LEFT:
                case TOP:
                case RIGHT:
                case BOTTOM:
                    if (corner != null) {
                        int x = corner.x + direction.x * MAX_MAPS;
                        x = Math.max(minX, Math.min(maxX + 1 - MAX_MAPS, x));
                        int y = corner.y + direction.y * MAX_MAPS;
                        y = Math.max(minY, Math.min(maxY + 1 - MAX_MAPS, y));
                        corner = Index.of(x, y);
                    }
                    break;
                case IN:
                    fixedMapDistance = true;
                    --mapDistance;
                    if(mapDistance == 1) {
                        changeGui(getSingleMap(container.getAtlasInventory().getMapInfos().get(corner)));
                    }
                    break;
                case OUT:
                    fixedMapDistance = true;
                    ++mapDistance;
                    break;
            }
        }

        @Override
        public boolean buttonVisible(ButtonDirection direction) {
            switch (direction) {
                case LEFT:
                case TOP:
                case RIGHT:
                case BOTTOM:
                    return corner != null;
                case IN:
                case OUT:
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean buttonEnabled(ButtonDirection direction) {
            switch (direction) {
                case LEFT:
                    return corner != null && corner.x > minX;
                case TOP:
                    return corner != null && corner.y > minY;
                case RIGHT:
                    return corner != null && corner.x + mapDistance <= maxX;
                case BOTTOM:
                    return corner != null && corner.y + mapDistance <= maxY;
                case IN:
                    return mapDistance > 1;
                case OUT:
                    return mapDistance < maxMapDistance;
                default:
                    return false;
            }
        }

        private double normalizeForMapArea(double base, double val) {
            return (val - base) / SIZE;
        }
    }

    private class SingleMap implements MapGui {
        private final Set<ButtonDirection> supportedDirections = EnumSet.of(ButtonDirection.LEFT, ButtonDirection.TOP, ButtonDirection.RIGHT, ButtonDirection.BOTTOM, ButtonDirection.BACK);
        private AtlasInventory.MapInfo mapInfo;

        public SingleMap(AtlasInventory.MapInfo mapInfo) {
            this.mapInfo = mapInfo;
        }

        @Override
        public void render(MatrixStack matrices, IRenderTypeBuffer.Impl bufferSource, int mouseX, int mouseY) {
            final Minecraft mc = Minecraft.getInstance();
            final World world = mc.world;
            if (world == null) {
                return;
            }
            if (mapInfo != null) {
                MapData mapData = world.getMapData(FilledMapItem.getMapName(mapInfo.id));
                if (mapData != null) {
                    matrices.push();
                    matrices.translate(0, 0, 1);
                    Minecraft.getInstance().gameRenderer.getMapItemRenderer().renderMap(matrices, bufferSource, mapData, true, LIGHT);
                    renderDecorations(matrices, bufferSource, mapData, 2f, it -> true);
                    matrices.pop();
                }
            }
        }

        @Override
        public void buttonClick(ButtonDirection direction) {
            if (direction == ButtonDirection.BACK) {
                changeGui(getWorldMap());
            } else {
                AtlasInventory inventory = container.getAtlasInventory();
                int mapX = inventory.convertCoordToIndex(mapInfo.x);
                int mapY = inventory.convertCoordToIndex(mapInfo.z);
                AtlasInventory.MapInfo mapInfo1 = inventory.getMapInfos().get(Index.of(mapX + direction.x, mapY + direction.y));
                if (mapInfo1 != null) {
                    changeGui(getSingleMap(mapInfo1));
                }
            }
        }

        @Override
        public boolean buttonVisible(ButtonDirection direction) {
            return supportedDirections.contains(direction);
        }

        @Override
        public boolean buttonEnabled(ButtonDirection direction) {
            AtlasInventory inventory = container.getAtlasInventory();
            Map<Index, AtlasInventory.MapInfo> mapInfos = inventory.getMapInfos();
            if (direction == ButtonDirection.BACK) {
                return mapInfo == null && !mapInfos.isEmpty() || mapInfos.size() > 1;
            }
            if (mapInfo != null) {
                int mapX = inventory.convertCoordToIndex(mapInfo.x);
                int mapY = inventory.convertCoordToIndex(mapInfo.z);
                return mapInfos.containsKey(Index.of(mapX + direction.x, mapY + direction.y));
            }
            return false;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && guiLeft + LEFT <= mouseX && mouseX < guiLeft + LEFT + SIZE && guiTop + TOP <= mouseY && mouseY < guiTop + TOP + SIZE) {
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
    }
}
