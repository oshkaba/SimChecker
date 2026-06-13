import csv
from statistics import mean
from datetime import datetime

class ExpenseTracker:
    def __init__(self):
        self.records = []

    def add_expense(self, date_text, category, amount):
        date = datetime.strptime(date_text, "%Y-%m-%d")
        self.records.append({"date": date, "category": category, "amount": float(amount)})

    def total_by_category(self):
        totals = {}
        for record in self.records:
            category = record["category"]
            totals[category] = totals.get(category, 0) + record["amount"]
        return totals

    def monthly_average(self):
        months = {}
        for record in self.records:
            key = record["date"].strftime("%Y-%m")
            months.setdefault(key, []).append(record["amount"])
        return {month: round(mean(values), 2) for month, values in months.items()}

    def export_summary(self, filename):
        totals = self.total_by_category()
        with open(filename, "w", newline="") as file:
            writer = csv.writer(file)
            writer.writerow(["Category", "Total"])
            for category, total in sorted(totals.items()):
                writer.writerow([category, round(total, 2)])

def main():
    tracker = ExpenseTracker()
    tracker.add_expense("2026-01-03", "food", 18.50)
    tracker.add_expense("2026-01-05", "books", 44.99)
    tracker.add_expense("2026-01-09", "food", 12.25)
    tracker.add_expense("2026-02-01", "transport", 30.00)
    tracker.add_expense("2026-02-04", "food", 21.75)
    tracker.add_expense("2026-02-10", "books", 15.00)
    print("Totals:", tracker.total_by_category())
    print("Monthly averages:", tracker.monthly_average())
    tracker.export_summary("expense_summary.csv")

if __name__ == "__main__":
    main()


