package com.hakimen.peripherals.peripherals;

import com.google.common.collect.ImmutableList;
import com.hakimen.peripherals.blocks.ScannerBlock;
import com.hakimen.peripherals.blocks.tile_entities.ScannerEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import dan200.computercraft.core.apis.ComputerAccess;
import dan200.computercraft.shared.media.items.PrintoutItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import static dan200.computercraft.core.util.ArgumentHelpers.assertBetween;

public class ScannerPeripheral implements IPeripheral, IPeripheralProvider {

    enum Types {
        WRITTEN_BOOK,
        PRINTED,
        WRITTEN_AND_SIGNED_BOOK,
        NONE

    }
    ScannerEntity entity;
    public ScannerPeripheral() {

    }
    @Override
    public String getType() {
        return "scanner";
    }
    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other == this;
    }

    public Types getStackType(){
        ItemStack stack = entity.inventory.getStackInSlot(0);
        if(stack.getItem() instanceof WrittenBookItem){
            return Types.WRITTEN_AND_SIGNED_BOOK;
        }

        if(stack.getItem() instanceof WritableBookItem){
            return Types.WRITTEN_BOOK;
        }

        if(stack.getItem() instanceof PrintoutItem){
            return Types.PRINTED;
        }

        return Types.NONE;
    }

    public static void loadPages(CompoundTag baseTag, Consumer<String> stringConsumer) {
        ListTag pagesTag = baseTag.getList("pages", 8).copy();
        IntFunction intPageMapper;
        if (Minecraft.getInstance().isTextFilteringEnabled() && baseTag.contains("filtered_pages", 10)) {
            CompoundTag filteredPagesTag = baseTag.getCompound("filtered_pages");
            intPageMapper = (pageNumber) -> {
                String value = String.valueOf(pageNumber);
                return filteredPagesTag.contains(value) ? filteredPagesTag.getString(value) : pagesTag.getString(pageNumber);
            };
        } else {
            Objects.requireNonNull(pagesTag);
            intPageMapper = pagesTag::getString;
        }

        for(int idx = 0; idx < pagesTag.size(); ++idx) {
            stringConsumer.accept((String)intPageMapper.apply(idx));
        }

    }

    static List<String> loadPages(CompoundTag tag) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        Objects.requireNonNull(builder);
        loadPages(tag, builder::add);
        return builder.build();
    }
    private static List<String> readPagesWrittenBook(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return (tag != null && WrittenBookItem.makeSureTagIsValid(tag) ? loadPages(tag) : ImmutableList.of(Component.Serializer.toJson(Component.translatable("book.invalid.tag").withStyle(ChatFormatting.DARK_RED))));
    }

    private static List<String> readPagesWritableBook(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return (tag != null && WritableBookItem.makeSureTagIsValid(tag) ? loadPages(tag) : ImmutableList.of(Component.Serializer.toJson(Component.translatable("book.invalid.tag").withStyle(ChatFormatting.DARK_RED))));
    }

    public FormattedText getPageRaw(List<String> pages, int page) {
        String temp = pages.get(page);

        try {
            FormattedText parsedText = Component.Serializer.fromJson(temp);
            if (parsedText != null) {
                return parsedText;
            }
        } catch (Exception ignored) {
        }

        return FormattedText.of(temp);
    }

    @LuaFunction
    public MethodResult getPage(int page) throws LuaException {
        page -= 1;
        ItemStack stack = entity.inventory.getStackInSlot(0);
        if(stack.equals(ItemStack.EMPTY)){
            return MethodResult.of(false, "No Book");
        }
        switch (getStackType()){
            case PRINTED -> {
                int pages = PrintoutItem.getPageCount(stack);
                assertBetween(page,0,pages, "Expected page number to be %s");
                String[] text = PrintoutItem.getText(stack);

                return MethodResult.of(true, text[page]);
            }

            case WRITTEN_AND_SIGNED_BOOK -> {
                List<String> bookText = readPagesWrittenBook(stack);
                int pages = WrittenBookItem.getPageCount(stack);
                assertBetween(page,0,pages, "Expected page number to be %s");

                return MethodResult.of(true, getPageRaw(bookText, page).getString());
            }

            case WRITTEN_BOOK -> {
                List<String> bookText = readPagesWritableBook(stack);
                int pages = bookText.size();
                assertBetween(page,0,pages, "Expected page number to be %s");

                return  MethodResult.of(true, FormattedText.of(bookText.get(page)).getString());
            }

            default -> {
                return MethodResult.of(false, "No scannable on scanner");
            }
        }
    }

    @LuaFunction
    public MethodResult getAuthor(){
        ItemStack stack = entity.inventory.getStackInSlot(0);
        if(stack.equals(ItemStack.EMPTY)){
            return MethodResult.of(false, "No Book");
        }
        if (Objects.requireNonNull(getStackType()) == Types.WRITTEN_AND_SIGNED_BOOK) {
            return MethodResult.of(true, stack.getOrCreateTag().getString(WrittenBookItem.TAG_AUTHOR));
        }
        return MethodResult.of(false, "No author");
    }

    @LuaFunction
    public MethodResult getTitle(){
        ItemStack stack = entity.inventory.getStackInSlot(0);
        if(stack.equals(ItemStack.EMPTY)){
            return MethodResult.of(false, "No Book");
        }
        switch (getStackType()) {
            case WRITTEN_AND_SIGNED_BOOK -> {
                return MethodResult.of(true, stack.getOrCreateTag().getString(WrittenBookItem.TAG_TITLE));
            }
            case PRINTED -> {
                return MethodResult.of(true, PrintoutItem.getTitle(stack));
            }
            default -> {
                return MethodResult.of(false, "No Title");
            }
        }
    }

    @LuaFunction
    public MethodResult getCopyStatus(){
        ItemStack stack = entity.inventory.getStackInSlot(0);
        switch (getStackType()) {
            case WRITTEN_AND_SIGNED_BOOK -> {
                return MethodResult.of(true, new String[]{"ORIGINAL", "COPY", "COPY OF COPY"}[WrittenBookItem.getGeneration(stack)]);
            }

            default -> {
                return MethodResult.of(false, "Not a Written Book");
            }
        }
    }


    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level world, @NotNull BlockPos pos, @NotNull Direction side) {
        if (world.getBlockState(pos).getBlock() instanceof ScannerBlock) {
            var playerInt = new ScannerPeripheral();
            playerInt.entity = (ScannerEntity) world.getBlockEntity(pos);
            return LazyOptional.of(() -> playerInt);
        }
        return LazyOptional.empty();
    }
}
