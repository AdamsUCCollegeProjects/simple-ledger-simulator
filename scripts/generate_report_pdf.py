#!/usr/bin/env python3
"""Generate a formatted academic PDF from REPORT.md."""

from __future__ import annotations

import re
from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY, TA_LEFT, TA_RIGHT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm, mm
from reportlab.platypus import (
    Image,
    KeepTogether,
    ListFlowable,
    ListItem,
    PageBreak,
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)

ROOT = Path(__file__).resolve().parents[1]
REPORT_MD = ROOT / "REPORT.md"
LOGO_PATH = ROOT / "logo.png"
OUTPUT_PDF = ROOT / "Blockchain_Ledger_Simulator_Report.pdf"

GOLD = colors.HexColor("#B8860B")
DARK = colors.HexColor("#2B2116")
MUTED = colors.HexColor("#5C5146")
RULE = colors.HexColor("#D9C7A0")
CODE_BG = colors.HexColor("#F7F3EA")
HEADER_BG = colors.HexColor("#F4EBD8")
CONTENT_WIDTH = 15.8 * cm
CODE_WRAP_WIDTH = 88
VERIFY_LINE = re.compile(
    r"^(Block #\d+:)\s+stored=([0-9a-fA-F]+)\s+"
    r"recalculated=([0-9a-fA-F]+)\s+(->\s+\S+)\s*$"
)

UNIVERSITY = "The University of Cambodia"
COURSE = "ITE412 (B)"
LECTURER = "Hang Youlay"
GROUP = "Group 1"
TOPIC = "Blockchain Ledger Simulator"
DATE = "September 14, 2026"

TEAM_MEMBERS = [
    "Yen Phary",
    "Chroeng Simleng",
    "Kech Kheang",
    "Phort Sopheakdei",
    "Samouen Rachana",
    "Sroem Meng",
    "Phorn Sros",
    "Ros Sopheak",
    "Seng Tharom",
]


def markdown_inline(text: str) -> str:
    escaped = (
        text.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
    )
    escaped = re.sub(r"`([^`]+)`", r'<font face="Courier" size="9">\1</font>', escaped)
    escaped = re.sub(r"\*\*([^*]+)\*\*", r"<b>\1</b>", escaped)
    return escaped


def make_styles():
    styles = getSampleStyleSheet()
    styles.add(
        ParagraphStyle(
            "CoverUniversity",
            fontName="Times-Bold",
            fontSize=18,
            leading=22,
            alignment=TA_CENTER,
            textColor=GOLD,
            spaceAfter=6,
        )
    )
    styles.add(
        ParagraphStyle(
            "CoverMeta",
            fontName="Times-Roman",
            fontSize=12,
            leading=16,
            alignment=TA_CENTER,
            textColor=DARK,
            spaceAfter=4,
        )
    )
    styles.add(
        ParagraphStyle(
            "CoverTitle",
            fontName="Times-Bold",
            fontSize=22,
            leading=26,
            alignment=TA_CENTER,
            textColor=DARK,
            spaceBefore=10,
            spaceAfter=8,
        )
    )
    styles.add(
        ParagraphStyle(
            "CoverLabel",
            fontName="Times-Bold",
            fontSize=11,
            leading=14,
            alignment=TA_CENTER,
            textColor=GOLD,
            spaceBefore=14,
            spaceAfter=4,
        )
    )
    styles.add(
        ParagraphStyle(
            "MemberName",
            fontName="Times-Roman",
            fontSize=11,
            leading=16,
            alignment=TA_LEFT,
            textColor=DARK,
        )
    )
    styles.add(
        ParagraphStyle(
            "DocHeading1",
            fontName="Times-Bold",
            fontSize=16,
            leading=20,
            textColor=GOLD,
            spaceBefore=16,
            spaceAfter=8,
            borderPadding=3,
        )
    )
    styles.add(
        ParagraphStyle(
            "DocHeading2",
            fontName="Times-Bold",
            fontSize=13,
            leading=17,
            textColor=DARK,
            spaceBefore=12,
            spaceAfter=6,
        )
    )
    styles.add(
        ParagraphStyle(
            "BodyJust",
            fontName="Times-Roman",
            fontSize=11,
            leading=16,
            alignment=TA_JUSTIFY,
            textColor=DARK,
            spaceAfter=8,
        )
    )
    styles.add(
        ParagraphStyle(
            "BulletBody",
            fontName="Times-Roman",
            fontSize=11,
            leading=15,
            alignment=TA_LEFT,
            textColor=DARK,
            leftIndent=12,
        )
    )
    styles.add(
        ParagraphStyle(
            "TableCell",
            fontName="Times-Roman",
            fontSize=9,
            leading=12,
            textColor=DARK,
        )
    )
    styles.add(
        ParagraphStyle(
            "TableHead",
            fontName="Times-Bold",
            fontSize=9,
            leading=12,
            textColor=DARK,
        )
    )
    styles.add(
        ParagraphStyle(
            "CodeBlock",
            fontName="Courier",
            fontSize=7.2,
            leading=9.6,
            textColor=DARK,
            alignment=TA_LEFT,
        )
    )
    styles.add(
        ParagraphStyle(
            "FooterStyle",
            fontName="Times-Italic",
            fontSize=8,
            textColor=MUTED,
            alignment=TA_CENTER,
        )
    )
    styles.add(
        ParagraphStyle(
            "HeaderLeft",
            fontName="Times-Roman",
            fontSize=8,
            textColor=GOLD,
            alignment=TA_LEFT,
        )
    )
    styles.add(
        ParagraphStyle(
            "HeaderRight",
            fontName="Times-Roman",
            fontSize=8,
            textColor=MUTED,
            alignment=TA_RIGHT,
        )
    )
    return styles


def draw_header_footer(canvas, doc):
    canvas.saveState()
    width, height = A4
    if doc.page > 1:
        canvas.setStrokeColor(GOLD)
        canvas.setLineWidth(0.6)
        canvas.line(2.2 * cm, height - 1.5 * cm, width - 2.2 * cm, height - 1.5 * cm)
        canvas.setFillColor(GOLD)
        canvas.setFont("Times-Roman", 8)
        canvas.drawString(2.2 * cm, height - 1.35 * cm, UNIVERSITY)
        canvas.setFillColor(MUTED)
        canvas.drawRightString(width - 2.2 * cm, height - 1.35 * cm, f"{COURSE}  |  {GROUP}")
        canvas.setStrokeColor(GOLD)
        canvas.line(2.2 * cm, 1.5 * cm, width - 2.2 * cm, 1.5 * cm)
        canvas.setFillColor(MUTED)
        canvas.setFont("Times-Italic", 8)
        canvas.drawString(2.2 * cm, 0.95 * cm, TOPIC)
        canvas.drawRightString(width - 2.2 * cm, 0.95 * cm, f"Page {doc.page}")
    canvas.restoreState()


def cover_page(styles):
    story = []
    story.append(Spacer(1, 0.6 * cm))
    if LOGO_PATH.exists():
        logo = Image(str(LOGO_PATH), width=5.2 * cm, height=5.2 * cm)
        logo.hAlign = "CENTER"
        story.append(logo)
    story.append(Spacer(1, 0.45 * cm))
    story.append(Paragraph(UNIVERSITY.upper(), styles["CoverUniversity"]))
    story.append(Paragraph("In Pursuit of Knowledge and Wisdom", styles["CoverMeta"]))
    story.append(Spacer(1, 0.25 * cm))

    line_data = [[""]]
    line = Table(line_data, colWidths=[8 * cm])
    line.setStyle(
        TableStyle(
            [
                ("LINEABOVE", (0, 0), (-1, 0), 1.2, GOLD),
                ("TOPPADDING", (0, 0), (-1, -1), 0),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 0),
            ]
        )
    )
    line.hAlign = "CENTER"
    story.append(line)
    story.append(Spacer(1, 0.55 * cm))
    story.append(Paragraph("PROJECT REPORT", styles["CoverLabel"]))
    story.append(Paragraph(TOPIC, styles["CoverTitle"]))
    story.append(
        Paragraph(
            "An Educational Java Simulator of SHA-256 Hashing,<br/>Block Linking, and Tamper Detection",
            styles["CoverMeta"],
        )
    )
    story.append(Spacer(1, 0.7 * cm))

    info_rows = [
        [Paragraph("<b>Course</b>", styles["TableHead"]), Paragraph(COURSE, styles["TableCell"])],
        [Paragraph("<b>Lecturer</b>", styles["TableHead"]), Paragraph(LECTURER, styles["TableCell"])],
        [Paragraph("<b>Group</b>", styles["TableHead"]), Paragraph(GROUP, styles["TableCell"])],
        [Paragraph("<b>Date</b>", styles["TableHead"]), Paragraph(DATE, styles["TableCell"])],
    ]
    info_table = Table(info_rows, colWidths=[3.4 * cm, 8.6 * cm])
    info_table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (0, -1), HEADER_BG),
                ("BOX", (0, 0), (-1, -1), 0.4, GOLD),
                ("INNERGRID", (0, 0), (-1, -1), 0.3, RULE),
                ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
                ("LEFTPADDING", (0, 0), (-1, -1), 8),
                ("RIGHTPADDING", (0, 0), (-1, -1), 8),
                ("TOPPADDING", (0, 0), (-1, -1), 6),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
            ]
        )
    )
    info_table.hAlign = "CENTER"
    story.append(info_table)

    story.append(Paragraph("TEAM MEMBERS", styles["CoverLabel"]))
    member_cells = []
    for index, name in enumerate(TEAM_MEMBERS, start=1):
        member_cells.append(Paragraph(f"{index}. {name}", styles["MemberName"]))
    left = member_cells[:5]
    right = member_cells[5:]
    while len(right) < len(left):
        right.append(Paragraph("", styles["MemberName"]))
    members = Table(
        [[left_item, right_item] for left_item, right_item in zip(left, right)],
        colWidths=[7.2 * cm, 7.2 * cm],
    )
    members.setStyle(
        TableStyle(
            [
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("LEFTPADDING", (0, 0), (-1, -1), 18),
                ("TOPPADDING", (0, 0), (-1, -1), 2),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 2),
            ]
        )
    )
    members.hAlign = "CENTER"
    story.append(members)
    story.append(PageBreak())
    return story


def wrap_at_width(line: str, width: int = CODE_WRAP_WIDTH) -> list[str]:
    if len(line) <= width:
        return [line]

    wrapped = []
    remaining = line
    is_continuation = False
    while remaining:
        prefix = "  " if is_continuation else ""
        budget = width - len(prefix)
        if len(remaining) <= budget:
            wrapped.append(prefix + remaining)
            break

        window = remaining[:budget]
        break_at = max(window.rfind(" "), window.rfind("#"), window.rfind("="))
        if break_at < budget // 4:
            break_at = budget
        wrapped.append(prefix + remaining[:break_at].rstrip())
        remaining = remaining[break_at:].lstrip()
        is_continuation = True
    return wrapped


def wrap_code_line(line: str) -> list[str]:
    match = VERIFY_LINE.match(line)
    if match:
        label, stored_hash, recalculated_hash, status = match.groups()
        return [
            label,
            f"  stored={stored_hash}",
            f"  recalculated={recalculated_hash}",
            f"  {status}",
        ]
    return wrap_at_width(line)


def code_block_flowable(code_lines: list[str], styles) -> Table:
    wrapped_lines = []
    for line in code_lines:
        wrapped_lines.extend(wrap_code_line(line.rstrip("\n")))
    if not wrapped_lines:
        wrapped_lines = [""]

    rows = []
    for line in wrapped_lines:
        safe = (
            line.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace(" ", "&nbsp;")
        )
        rows.append([Paragraph(safe if safe else "&nbsp;", styles["CodeBlock"])])

    table = Table(rows, colWidths=[CONTENT_WIDTH], repeatRows=0)
    table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, -1), CODE_BG),
                ("BOX", (0, 0), (-1, -1), 0.3, RULE),
                ("LEFTPADDING", (0, 0), (-1, -1), 6),
                ("RIGHTPADDING", (0, 0), (-1, -1), 6),
                ("TOPPADDING", (0, 0), (-1, -1), 0.6),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 0.6),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
            ]
        )
    )
    return table


def parse_table(lines: list[str], styles) -> Table:
    rows = []
    for line in lines:
        if re.match(r"^\|?\s*-+", line):
            continue
        cells = [cell.strip() for cell in line.strip().strip("|").split("|")]
        if not cells:
            continue
        style = styles["TableHead"] if not rows else styles["TableCell"]
        rows.append([Paragraph(markdown_inline(cell), style) for cell in cells])
    col_count = max(len(row) for row in rows)
    usable = 16.6 * cm
    col_widths = [usable / col_count] * col_count
    table = Table(rows, colWidths=col_widths, repeatRows=1)
    table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), HEADER_BG),
                ("TEXTCOLOR", (0, 0), (-1, 0), DARK),
                ("FONTNAME", (0, 0), (-1, 0), "Times-Bold"),
                ("BOX", (0, 0), (-1, -1), 0.4, GOLD),
                ("INNERGRID", (0, 0), (-1, -1), 0.25, RULE),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("LEFTPADDING", (0, 0), (-1, -1), 6),
                ("RIGHTPADDING", (0, 0), (-1, -1), 6),
                ("TOPPADDING", (0, 0), (-1, -1), 5),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
            ]
        )
    )
    return table


def body_from_markdown(styles) -> list:
    text = REPORT_MD.read_text(encoding="utf-8")
    lines = text.splitlines()
    story = []
    index = 0
    while index < len(lines):
        line = lines[index]
        if line.startswith("# "):
            index += 1
            continue
        if line.startswith("## "):
            story.append(Paragraph(markdown_inline(line[3:].strip()), styles["DocHeading1"]))
            index += 1
            continue
        if line.startswith("### "):
            story.append(Paragraph(markdown_inline(line[4:].strip()), styles["DocHeading2"]))
            index += 1
            continue
        if line.startswith("```"):
            index += 1
            code_lines = []
            while index < len(lines) and not lines[index].startswith("```"):
                code_lines.append(lines[index])
                index += 1
            index += 1
            story.append(Spacer(1, 2 * mm))
            story.append(code_block_flowable(code_lines, styles))
            story.append(Spacer(1, 3 * mm))
            continue
        if line.startswith("|"):
            table_lines = []
            while index < len(lines) and lines[index].startswith("|"):
                table_lines.append(lines[index])
                index += 1
            story.append(Spacer(1, 2 * mm))
            story.append(parse_table(table_lines, styles))
            story.append(Spacer(1, 3 * mm))
            continue
        if re.match(r"^[-*] ", line) or re.match(r"^\d+\. ", line):
            items = []
            numbered = bool(re.match(r"^\d+\. ", line))
            while index < len(lines) and (
                re.match(r"^[-*] ", lines[index]) or re.match(r"^\d+\. ", lines[index])
            ):
                item_text = re.sub(r"^([-*] |\d+\. )", "", lines[index]).strip()
                items.append(
                    ListItem(
                        Paragraph(markdown_inline(item_text), styles["BulletBody"]),
                        leftIndent=12,
                        bulletColor=GOLD,
                    )
                )
                index += 1
            if numbered:
                story.append(
                    ListFlowable(
                        items,
                        bulletType="1",
                        start="1",
                        leftIndent=18,
                        bulletFontName="Times-Bold",
                        bulletFontSize=10,
                        spaceAfter=8,
                    )
                )
            else:
                story.append(
                    ListFlowable(
                        items,
                        bulletType="bullet",
                        leftIndent=18,
                        bulletFontName="Times-Roman",
                        bulletFontSize=11,
                        spaceAfter=8,
                    )
                )
            continue
        if not line.strip():
            index += 1
            continue
        paragraph_lines = [line]
        index += 1
        while (
            index < len(lines)
            and lines[index].strip()
            and not lines[index].startswith("#")
            and not lines[index].startswith("```")
            and not lines[index].startswith("|")
            and not re.match(r"^[-*] ", lines[index])
            and not re.match(r"^\d+\. ", lines[index])
        ):
            paragraph_lines.append(lines[index])
            index += 1
        story.append(Paragraph(markdown_inline(" ".join(paragraph_lines)), styles["BodyJust"]))
    return story


def build_pdf() -> Path:
    styles = make_styles()
    document = SimpleDocTemplate(
        str(OUTPUT_PDF),
        pagesize=A4,
        leftMargin=2.2 * cm,
        rightMargin=2.2 * cm,
        topMargin=2.2 * cm,
        bottomMargin=2.2 * cm,
        title=f"{TOPIC} — Project Report",
        author=f"{GROUP}, {UNIVERSITY}",
        subject=f"{COURSE} project report for {LECTURER}",
    )
    story = cover_page(styles)
    story.extend(body_from_markdown(styles))
    document.build(story, onFirstPage=draw_header_footer, onLaterPages=draw_header_footer)
    return OUTPUT_PDF


if __name__ == "__main__":
    pdf_path = build_pdf()
    print(pdf_path)
