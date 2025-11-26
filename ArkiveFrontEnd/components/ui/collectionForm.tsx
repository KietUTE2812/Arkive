import { Button } from "./button";
import { FormField } from "./form";
import { Input } from "./input";
import { SimpleModal } from "./modal";
import { useState } from "react";

const CollectionForm = ({ open, setOpen, onSubmit }: { open: boolean, setOpen: (open: boolean) => void, onSubmit: (data: any) => void }) => {
    const [formData, setFormData] = useState<{ name: string, description: string }>({
        name: "",
        description: "",
    });
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };
    const handleSubmit = () => {
        onSubmit(formData);
    };
    return (
        <SimpleModal
            open={open}
            onOpenChange={setOpen}
            title="Thêm mới bộ sưu tập"
            description="Thêm mới bộ sưu tập"
            children={<div className="space-y-4">
                <FormField label="Name" error={formData.name ? "" : "Name is required"} children={<Input type="text" name="name" value={formData.name} onChange={handleChange} />} />
                <FormField label="Description" error={formData.description ? "" : "Description is required"} children={<Input type="text" name="description" value={formData.description} onChange={handleChange} />} />
                <Button type="submit" onClick={handleSubmit}>Thêm mới</Button>
            </div>}
        />
    );
};

export default CollectionForm;