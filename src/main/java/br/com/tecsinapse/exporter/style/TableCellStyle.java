/*
 * Tecsinapse Data Input and Output
 *
 * License: GNU Lesser General Public License (LGPL), version 3 or later
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package br.com.tecsinapse.exporter.style;

import static br.com.tecsinapse.exporter.style.Style.TABLE_CELL_STYLE_FOOTER;
import static br.com.tecsinapse.exporter.style.Style.TABLE_CELL_STYLE_HEADER;
import static br.com.tecsinapse.exporter.util.WorkbookUtil.toRgbByte;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

public class TableCellStyle {

    private HSSFColor backgroundColor;
    private HSSFColor fontColor;
    private CellVAlign vAlign;
    private CellHAlign hAlign;
    private CellStyleBorder border = Style.CELL_STYLE_BORDER_DEFAULT;
    private Integer fontSize;
    private boolean bold = false;
    private boolean italic = false;
    private boolean underline = false;
    private boolean strikeout = false;
    private boolean wrapText = false;
    private String cellFormat;
    private boolean cssWhiteAsTransparent = true;
    private boolean ignoreCssStyle;
    private String cssClass;

    public TableCellStyle(HSSFColor backgroundColor, String cssClass, boolean ignoreCssStyle) {
        this.backgroundColor = backgroundColor;
        this.ignoreCssStyle = ignoreCssStyle;
        this.cssClass = cssClass;
        this.fontSize = 10;
    }

    public TableCellStyle(HSSFColor backgroundColor) {
        this(backgroundColor, null, false);
    }

    public HSSFColor getBackgroundColor() {
        return backgroundColor;
    }

    public HSSFColor getFontColor() {
        return fontColor;
    }

    public void setFontColor(HSSFColor fontColor) {
        this.fontColor = fontColor;
    }

    public void setBackgroundColor(HSSFColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public boolean isStrikeout() {
        return strikeout;
    }

    public void setStrikeout(boolean strikeout) {
        this.strikeout = strikeout;
    }

    public boolean isWrapText() {
        return wrapText;
    }

    public void setWrapText(boolean wrapText) {
        this.wrapText = wrapText;
    }

    public CellVAlign getvAlign() {
        return vAlign;
    }

    public void setvAlign(CellVAlign vAlign) {
        this.vAlign = vAlign;
    }

    public CellHAlign gethAlign() {
        return hAlign;
    }

    public void sethAlign(CellHAlign hAlign) {
        this.hAlign = hAlign;
    }

    public CellStyleBorder getBorder() {
        return border;
    }

    public void setBorder(CellStyleBorder border) {
        this.border = border;
    }

    public String getCellFormat() {
        return cellFormat;
    }

    public void setCellFormat(String cellFormat) {
        this.cellFormat = cellFormat;
    }

    public boolean isHeader() {
        return this.equals(TABLE_CELL_STYLE_HEADER);
    }

    public boolean isFooter() {
        return this.equals(TABLE_CELL_STYLE_FOOTER);
    }

    public boolean isCssWhiteAsTransparent() {
        return cssWhiteAsTransparent;
    }

    public void setCssWhiteAsTransparent(boolean cssWhiteAsTransparent) {
        this.cssWhiteAsTransparent = cssWhiteAsTransparent;
    }

    public boolean isIgnoreCssStyle() {
        return ignoreCssStyle;
    }

    public void setIgnoreCssStyle(boolean ignoreCssStyle) {
        this.ignoreCssStyle = ignoreCssStyle;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getCssStyle() {
        if (ignoreCssStyle) {
            return null;
        }
        StringBuilder css = new StringBuilder();
        if (getBackgroundColor() != null) {
            css.append(CssStyle.toBackgroundColor(backgroundColor, cssWhiteAsTransparent));
        }
        if (getBorder() != null) {
            css.append(border.toCss());
        }
        if (getvAlign() != null) {
            css.append(vAlign.getCss());
        }
        if (gethAlign() != null) {
            css.append(hAlign.getCss());
        }
        configCssFont(css);
        return css.length() > 0 ? css.toString() : null;
    }

    public CellStyle toCellStyle(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();

        if (getBackgroundColor() != null) {
            if (cellStyle instanceof XSSFCellStyle) {
                ((XSSFCellStyle)cellStyle).setFillForegroundColor(new XSSFColor(toRgbByte(getBackgroundColor())));
            } else {
                cellStyle.setFillForegroundColor(getBackgroundColor().getIndex());
            }
            cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        }
        if (getBorder() != null) {
            cellStyle = border.toCellStyle(cellStyle);
        }
        if (getvAlign() != null) {
            cellStyle.setVerticalAlignment(vAlign.getCellStyleVAlign());
        }
        if (gethAlign() != null) {
            cellStyle.setAlignment(hAlign.getCellStyleHAlign());
        }

        cellStyle.setWrapText(isWrapText());

        Font font = wb.createFont();
        configFont(font);
        cellStyle.setFont(font);
        if (cellFormat != null && !cellFormat.isEmpty()) {
            cellStyle.setDataFormat(wb.createDataFormat().getFormat(cellFormat));
        }
        return cellStyle;
    }

    private void configFont(Font font) {
        font.setBold(isBold());
        font.setItalic(isItalic());
        font.setStrikeout(isStrikeout());
        font.setUnderline(isUnderline() ? Font.U_SINGLE : Font.U_NONE);
        if (getFontSize() != null) {
            font.setFontHeightInPoints(fontSize.shortValue());
        }
        if (getFontColor() != null) {
            if (font instanceof XSSFFont) {
                ((XSSFFont)font).setColor(new XSSFColor(toRgbByte(fontColor)));
            } else {
                font.setColor(fontColor.getIndex());
            }
        }
    }

    private void configCssFont(StringBuilder builder) {
        if (isBold()) {
            builder.append(CssStyle.BOLD.getCss());
        }
        if (isItalic()) {
            builder.append(CssStyle.ITALIC.getCss());
        }
        if (isStrikeout()) {
            builder.append(CssStyle.STRIKEOUT.getCss());
        }
        if (isUnderline()) {
            builder.append(CssStyle.UNDERLINE.getCss());
        }
        if (getFontSize() != null) {
            builder.append(CssStyle.toFontSize(fontSize));
        }
        if (getFontColor() != null) {
            builder.append(CssStyle.toTextColor(fontColor));
        }
    }

    public TableCellStyle duplicate() {
        TableCellStyle tcs = new TableCellStyle(getBackgroundColor());
        CellStyleBorder csb = getBorder();
        if (csb != null) {
            tcs.setBorder(csb.duplicate());
        } else {
            tcs.setBorder(null);
        }

        tcs.setvAlign(getvAlign());
        tcs.sethAlign(gethAlign());

        tcs.setFontColor(getFontColor());
        tcs.setFontSize(getFontSize());

        tcs.setBold(isBold());
        tcs.setStrikeout(isStrikeout());
        tcs.setWrapText(isWrapText());
        tcs.setItalic(isItalic());
        tcs.setUnderline(isUnderline());
        tcs.setCssWhiteAsTransparent(isCssWhiteAsTransparent());
        tcs.setIgnoreCssStyle(isIgnoreCssStyle());
        tcs.setCssClass(getCssClass());
        return tcs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof TableCellStyle)) {
            return false;
        }

        final TableCellStyle that = (TableCellStyle) o;

        if (bold != that.bold) {
            return false;
        }
        if (italic != that.italic) {
            return false;
        }
        if (underline != that.underline) {
            return false;
        }
        if (strikeout != that.strikeout) {
            return false;
        }
        if (wrapText != that.wrapText) {
            return false;
        }
        if (cssWhiteAsTransparent != that.isCssWhiteAsTransparent()) {
            return false;
        }
        if (ignoreCssStyle != that.isIgnoreCssStyle()) {
            return false;
        }
        if (backgroundColor != null ? !backgroundColor.equals(that.backgroundColor) : that.backgroundColor != null) {
            return false;
        }
        if (fontColor != null ? !fontColor.equals(that.fontColor) : that.fontColor != null) {
            return false;
        }
        if (vAlign != that.vAlign) {
            return false;
        }
        if (hAlign != that.hAlign) {
            return false;
        }
        if (border != null ? !border.equals(that.border) : that.border != null) {
            return false;
        }
        if (cellFormat != null && !cellFormat.equals(that.cellFormat)) {
            return false;
        }
        if (cssClass != null && !cssClass.equals(that.cssClass)) {
            return false;
        }
        return fontSize != null ? fontSize.equals(that.fontSize) : that.fontSize == null;

    }

    @Override
    public int hashCode() {
        int result = backgroundColor != null ? backgroundColor.hashCode() : 0;
        result = 31 * result + (fontColor != null ? fontColor.hashCode() : 0);
        result = 31 * result + (vAlign != null ? vAlign.hashCode() : 0);
        result = 31 * result + (hAlign != null ? hAlign.hashCode() : 0);
        result = 31 * result + (border != null ? border.hashCode() : 0);
        result = 31 * result + (fontSize != null ? fontSize.hashCode() : 0);
        result = 31 * result + (bold ? 1 : 0);
        result = 31 * result + (italic ? 1 : 0);
        result = 31 * result + (underline ? 1 : 0);
        result = 31 * result + (strikeout ? 1 : 0);
        result = 31 * result + (wrapText ? 1 : 0);
        result = 31 * result + (ignoreCssStyle ? 1 : 0);
        result = 31 * result + (cssWhiteAsTransparent ? 1 : 0);
        result = 31 * result + (cssClass != null ? cssClass.hashCode() : 0);
        return result;
    }

    public String getCssBlockClassName() {
        return String.format("tbcs-%s", cssClass != null
                ? cssClass
                : Integer.toHexString(System.identityHashCode(this)));
    }

    public String getCssBlock() {
        return String.format(".%s {%s}\n",
                getCssBlockClassName(),
                getCssStyle());
    }
}
